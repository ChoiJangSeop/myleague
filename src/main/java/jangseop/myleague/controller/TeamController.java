package jangseop.myleague.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.TeamAssembler;
import jangseop.myleague.domain.Team;
import jangseop.myleague.dto.TeamDto;
import jangseop.myleague.exception.TeamNotFoundException;
import jangseop.myleague.repository.TeamRepository;
import jangseop.myleague.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Api(tags = {"1. Team"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final TeamAssembler teamAssembler;

    @ApiOperation(value = "팀 전체 조회", notes = "전체 팀을 조회한다.")
    @GetMapping("/teams")
    public CollectionModel<EntityModel<TeamDto>> all() {

        List<EntityModel<TeamDto>> teams = teamRepository.findAll().stream()
                .map(teamAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(teams, linkTo(methodOn(TeamController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "팀 단건 조회", notes = "입력 받은 id에 해당하는 팀을 조회한다.")
    @GetMapping("/teams/{teamId}")
    public EntityModel<TeamDto> one(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Long teamId) {
        Team team = teamRepository.findOne(teamId);

        // TODO [refactoring] convert optional type
        if (team == null) {
            throw new TeamNotFoundException(teamId);
        }

        return teamAssembler.toModel(team);
    }

    @ApiOperation(value = "팀 생성", notes = "팀을 생성해 데이터베이스에 저장한다.")
    @PostMapping("/teams")
    public EntityModel<TeamDto> newTeam(
            @ApiParam(value = "팀 입력 DTO(name/stat)", required = true) @RequestBody TeamDto dto) {
        Long teamId = teamService.create(dto.getName(), dto.getShortName(), dto.getStat());
        return teamAssembler.toModel(teamRepository.findOne(teamId));
    }

    @ApiOperation(value = "팀 수정", notes = "입력한 id에 해당하는 팀을 수정한다")
    @PutMapping("/teams/{teamId}")
    public ResponseEntity<?> replaceTeam(
            @ApiParam(value="수정될 팀 아이디", required = true) @PathVariable Long teamId,
            @ApiParam(value="팀 수정사항 DTO", required = true) @RequestBody TeamDto dto) {
        Team team = teamService.update(teamId, dto.getName(), dto.getShortName(), dto.getStat());

        EntityModel<TeamDto> entityModel = teamAssembler.toModel(team);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @ApiOperation(value = "팀 삭제", notes = "입력한 id에 해당하는 팀을 삭제한다")
    @DeleteMapping("/teams/{teamId}")
    public void deleteTeam(
            @ApiParam(value="삭제될 팀 아이디", required = true) @PathVariable Long teamId) {
        teamService.inactiveTeam(teamId);
    }

    @ApiOperation(value = "팀 재복구", notes = "삭제된 팀을 다시 복구합니다.")
    @PutMapping("/teams/{teamId}/activate")
    public void activateTeam(
            @ApiParam(value = "복구할 팀의 아이디", required = true) @PathVariable Long teamId) {
        teamService.activateTeam(teamId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalStateException.class)
    public Map<String, String> handle(IllegalStateException e) {
        log.error(e.getMessage(), e);
        Map<String, String> errorAttributes = new HashMap<>();

        errorAttributes.put("code", "NOT_VALIDATE");
        errorAttributes.put("msg", e.getMessage());
        return errorAttributes;
    }

}
