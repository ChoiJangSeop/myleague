package jangseop.myleague.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.MatchAssembler;
import jangseop.myleague.domain.Match;
import jangseop.myleague.dto.MatchDto;
import jangseop.myleague.repository.MatchRepository;
import jangseop.myleague.repository.ParticipantRepository;
import jangseop.myleague.repository.TeamRepository;
import jangseop.myleague.service.MatchService;
import jangseop.myleague.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = {"5. Match"})
@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchAssembler matchAssembler;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final ParticipantRepository participantRepository;

    @ApiOperation(value = "경기 전체 조회", notes = "데이터베이스에 저장된 모든 경기를 조회합니다.")
    @GetMapping("/matches")
    public CollectionModel<EntityModel<MatchDto>> all() {
        List<EntityModel<MatchDto>> matches = matchRepository.findAll().stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(matches,
                linkTo(methodOn(MatchController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "경기 단건 조회", notes = "입력 받은 아이디에 해당하는 경기를 조회합니다.")
    @GetMapping("/matches/{id}")
    public EntityModel<MatchDto> one(
            @ApiParam(value = "경기 아이디", required = true) @PathVariable Long id) {
        return matchAssembler.toModel(matchRepository.findOne(id));
    }

    @ApiOperation(value = "경기 생성", notes = "입력 받은 경기를 생성하고 데이터베이스 저장합니다.")
    @PostMapping("/matches")
    public EntityModel<MatchDto> newMatch(
            @ApiParam(value = "생성할 선수 정보", required = true) @RequestBody MatchDto dto) {
        Match match = matchService.create(dto.getRound(), dto.getMatchDate(), dto.getHomeId(), dto.getAwayId());
        return matchAssembler.toModel(match);
    }

    @ApiOperation(value = "경기 결과 입력", notes = "경기 결과를 입력하고, 참가팀의 전적을 갱신합니다.")
    @PutMapping("/matches/{id}/play")
    public ResponseEntity<?> playMatch(
            @ApiParam(value = "결과를 입력할 경기의 아이디", required = true) @PathVariable Long id,
            @ApiParam(value = "경기 결과", required = true) @RequestBody MatchDto dto) {
        Match match = matchService.playMatch(id, dto.getHomeScore(), dto.getAwayScore());
        EntityModel<MatchDto> entityModel = matchAssembler.toModel(match);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @ApiOperation(value = "경기 결과 취소", notes = "입력된 경기 결과를 초기화하고, 전적을 갱신합니다.")
    @PutMapping("/matches/{id}/cancel")
    public ResponseEntity<?> cancelMatch(
            @ApiParam(value = "결과 취소할 경기의 아이디", required = true) @PathVariable Long id) {
        Match match = matchService.cancelMatch(id);

        EntityModel<MatchDto> entityModel = matchAssembler.toModel(match);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @ApiOperation(value = "경기 검색", notes = "팀, 리그 정보를 통해 경기를 검색합니다.")
    @GetMapping("/matches/search")
    public CollectionModel<EntityModel<MatchDto>> searchMatch(
            @ApiParam(value = "경기 검색 파라미터 : team/league") @RequestParam Map<String, String> param) {

        Long teamId = null;
        Long leagueId = null;

        if (param.get("team") != null) {
            teamId = Long.parseLong(param.get("team"));
        }

        if (param.get("league") != null) {
            leagueId = Long.parseLong(param.get("league"));
        }

        List<EntityModel<MatchDto>> matches = matchService.searchMatch(teamId, leagueId).stream()
                .map(matchAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(matches,
                linkTo(methodOn(MatchController.class).searchMatch(param)).withSelfRel());
    }
}
