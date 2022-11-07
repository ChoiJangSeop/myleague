package jangseop.myleague.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.ParticipantAssembler;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.domain.ParticipantSearch;
import jangseop.myleague.dto.ParticipantDto;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.service.ParticipantService;
import lombok.RequiredArgsConstructor;
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

    @ApiOperation(value = "참가팀 전제 조회", notes = "데이터베이스에 저장된 참가팀을 모두 조회합니다.")
    @GetMapping("/participants")
    public CollectionModel<EntityModel<ParticipantDto>> all() {

        List<EntityModel<ParticipantDto>> participants = participantRepository.findAll(new ParticipantSearch()).stream()
                .map(participantAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(participants, linkTo(methodOn(ParticipantController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "참가팀 단건 조회", notes = "입력 받은 id에 해당하는 참가팀을 조회합니다")
    @GetMapping("/participants/{id}")
    public EntityModel<ParticipantDto> one(
            @ApiParam(value = "참가팀 아이디", required = true) @PathVariable Long id) {

        Participant participant = participantRepository.findOne(id);

        return participantAssembler.toModel(participant);
    }

    @ApiOperation(value = "참가팀 생성", notes = "입력된 정보의 참가팀을 생성하여 데이터베이스에 저장합니다.")
    @PostMapping("/participants")
    public EntityModel<ParticipantDto> newParticipant(
            @ApiParam(value = "생성할 참가팀 정보", required = true) @RequestBody ParticipantDto dto) {

        Participant participant = participantService.create(dto.getTeamId(), dto.getLeagueId());

        return participantAssembler.toModel(participant);
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
