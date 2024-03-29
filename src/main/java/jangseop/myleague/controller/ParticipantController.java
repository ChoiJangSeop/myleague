package jangseop.myleague.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.MatchAssembler;
import jangseop.myleague.assembler.ParticipantAssembler;
import jangseop.myleague.assembler.RecordAssembler;
import jangseop.myleague.domain.Match;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.ParticipantSearch;
import jangseop.myleague.domain.Playoff;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.dto.MatchDto;
import jangseop.myleague.dto.ParticipantDto;
import jangseop.myleague.dto.RecordDto;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.hibernate.EntityMode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = {"4. Participant"})
@RestController
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;
    private final ParticipantAssembler participantAssembler;
    private final RecordAssembler recordAssembler;
    private final MatchAssembler matchAssembler;

    @ApiOperation(value = "참가팀 전제 조회", notes = "데이터베이스에 저장된 참가팀을 모두 조회합니다.")
    @GetMapping("/participants")
    public CollectionModel<EntityModel<ParticipantDto>> all() {

        List<EntityModel<ParticipantDto>> participants = participantRepository.findAll(new ParticipantSearch()).stream()
                .map(participantAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(participants, linkTo(methodOn(ParticipantController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "참가팀 기록 전체 조회", notes = "데이터 베이스의 저장된 특정 참가팀의 모든 기록을 조회합니다.")
    @GetMapping("/participants/{id}/records")
    public CollectionModel<EntityModel<RecordDto>> allRecords(
            @ApiParam(value = "참가팀 아이디", required = true) @PathVariable Long id) {
        Participant findParticipant = participantRepository.findOne(id);

        List<EntityModel<RecordDto>> records = findParticipant.getRecords().stream()
                .map(recordAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(records, linkTo(methodOn(ParticipantController.class).allRecords(id)).withSelfRel());
    }

    @ApiOperation(value = "참가팀 단건 조회", notes = "입력 받은 id에 해당하는 참가팀을 조회합니다")
    @GetMapping("/participants/{id}")
    public EntityModel<ParticipantDto> one(
            @ApiParam(value = "참가팀 아이디", required = true) @PathVariable Long id) {

        Participant participant = participantRepository.findOne(id);

        return participantAssembler.toModel(participant);
    }

    @ApiOperation(value = "참가팀 기록 단건 조회", notes = "데이터베이스의 저장된 기록을 단건 조회합니다.")
    @GetMapping("/participants/{id}/records/{round}")
    public EntityModel<RecordDto> oneRecord(
            @ApiParam(value = "참가팀 아이디", required = true) @PathVariable Long id,
            @ApiParam(value = "라운드", required = true) @PathVariable int round) {
        List<Record> findRecords = participantRepository.findOne(id).getRecords()
                .stream().filter(record -> record.getRound() == round)
                .collect(Collectors.toList());

        if (findRecords.size() > 0) {
            return recordAssembler.toModel(findRecords.get(0));
        } else {
            throw new IllegalStateException("존재하지 않는 라운드입니다");
        }
    }

    @ApiOperation(value = "참가팀 생성", notes = "입력된 정보의 참가팀을 생성하여 데이터베이스에 저장합니다.")
    @PostMapping("/participants")
    public EntityModel<ParticipantDto> newParticipant(
            @ApiParam(value = "생성할 참가팀 정보", required = true) @RequestBody ParticipantDto dto) {

        Participant participant = participantService.create(dto.getTeamId(), dto.getLeagueId());

        return participantAssembler.toModel(participant);
    }

    @ApiOperation(value = "기록 생성", notes ="입력된 라운드의 참가팀 기록을 생성하여 데이터베이스에 저장합니다.")
    @PostMapping("/participants/{id}/record")
    public EntityModel<RecordDto> newRecord(
            @ApiParam(value = "기록을 생성할 참가팀의 아이디", required = true) @PathVariable Long id,
            @ApiParam(value = "생성할 참가팀 기록의 정보", required = true) @RequestBody RecordDto dto) {
        Record record = participantService.addRecord(id, dto.getRound(), dto.getType());

        return recordAssembler.toModel(record);
    }

    @ApiOperation(value = "특정 라운드 모든 경기 검색", notes="입력된 라운드의 모든 경기를 출력합니다")
    @GetMapping("/participants/{id}/records/{round}/matches")
    public CollectionModel<EntityModel<MatchDto>> roundAllMatches(
            @ApiParam(value = "참가팀의 아이디", required = true) @PathVariable Long id,
            @ApiParam(value = "라운드", required = true) @PathVariable int round) {
        List<EntityModel<MatchDto>> matches = participantService.getRoundMatches(id, round).stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(matches, linkTo(methodOn(ParticipantController.class).roundAllMatches(id, round)).withSelfRel());
    }

    @ApiOperation(value = "참가팀 검색", notes = "팀/리그 정보를 입력하여 참가팀을 검색합니다.")
    @GetMapping("/participants/search")
    public CollectionModel<EntityModel<ParticipantDto>> search(
            @ApiParam(value = "참가팀 검색 파라미터 (팀 아이디 / 리그 아이디)") @RequestParam Map<String, String> param) {

        // TODO null processing
        Long teamId = null;
        Long leagueId = null;

        if (param.get("team") != null) {
            teamId = Long.parseLong(param.get("team"));
        }

        if (param.get("league") != null) {
            leagueId = Long.parseLong(param.get("league"));
        }

        List<EntityModel<ParticipantDto>> participants = participantService.searchParticipants(teamId, leagueId).stream()
                .map(participantAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(participants, linkTo(methodOn(ParticipantController.class).search(param)).withSelfRel());
    }

    @ApiOperation(value = "참가팀 삭제", notes = "입력한 id에 해당하는 참가팀을 삭제한다")
    @DeleteMapping("/participants/{participantId}")
    public void deleteTeam(
            @ApiParam(value="삭제될 참가팀 아이디", required = true) @PathVariable Long participantId) {
        participantService.delete(participantId);
    }
}
