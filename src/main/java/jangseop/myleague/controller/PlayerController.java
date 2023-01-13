package jangseop.myleague.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.PlayerAssembler;
import jangseop.myleague.domain.Player;
import jangseop.myleague.domain.PlayerSearch;
import jangseop.myleague.dto.PlayerDto;
import jangseop.myleague.exception.PlayerNotFoundException;
import jangseop.myleague.repository.PlayerRepository;
import jangseop.myleague.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Collection;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Api(tags = {"2. Player"})
@RestController
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final PlayerRepository playerRepository;
    private final PlayerAssembler playerAssembler;

    @ApiOperation(value = "선수 전체 조회", notes = "데이터베이스 내에 모든 선수를 조회합니다.")
    @GetMapping("/players")
    public CollectionModel<EntityModel<PlayerDto>> all() {

        List<EntityModel<PlayerDto>> players = playerRepository.findAll().stream()
                .map(playerAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(players, linkTo(methodOn(PlayerController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "선수 단건 조회", notes = "입력 받은 id에 해당하는 선수를 조회합니다.")
    @GetMapping("/players/{id}")
    public EntityModel<PlayerDto> one(
            @ApiParam(value = "조회할 팀 아이디", required = true) @PathVariable Long id) {
        Player player = playerRepository.findOne(id);

        if (player == null) {
            throw new PlayerNotFoundException(id);
        }

        return playerAssembler.toModel(player);
    }

    @ApiOperation(value = "선수 생성", notes = "선수를 생성하여 데이터베이스에 저장합니다.")
    @PostMapping("/players")
    public EntityModel<PlayerDto> newPlayer(
            @ApiParam(value = "생성할 선수의 정보 (name/position/stat)") @RequestBody PlayerDto dto) {
        Player player = playerService.create(dto.getName(), dto.getPosition(), dto.getStat());
        return playerAssembler.toModel(player);
    }

    @ApiOperation(value = "선수 수정", notes = "입력 받은 아이디에 해당하는 선수의 정보를 수정합니다.")
    @PutMapping("/players/{id}")
    public ResponseEntity<?> replacePlayer(
            @ApiParam(value = "수정할 선수의 아이디", required = true) @PathVariable Long id,
            @ApiParam(value = "수정할 정보 사항", required = true) @RequestBody PlayerDto dto) {
        Player player = playerService.update(id, dto.getName(), dto.getPosition(), dto.getStat());

        EntityModel<PlayerDto> entityModel = playerAssembler.toModel(player);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @ApiOperation(value = "선수 팀 등록", notes = "입력 받은 id에 해당하는 선수를 입력받은 팀id에 해당하는 팀에 등록합니다.")
    @PutMapping("/players/{id}/register/{teamId}")
    public ResponseEntity<?> registerPlayer(
            @ApiParam(value = "팀 등록할 선수의 아이디") @PathVariable Long id,
            @ApiParam(value = "등록할 팀의 아이디") @PathVariable Long teamId) {
        Player player = playerService.registerTeam(teamId, id);

        EntityModel<PlayerDto> entityModel = playerAssembler.toModel(player);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @ApiOperation(value = "선수 팀 등록 해제", notes = "입력 받은 id에 해당하는 선수의 등록된 팀을 해제합니다.")
    @PutMapping("/players/{id}/deregister")
    public ResponseEntity<?> deregisterPlayer(
            @ApiParam(value = "팀 등록을 해제할 선수의 아이디", required = true) @PathVariable Long id) {
        Player player = playerService.deregisterTeam(id);

        EntityModel<PlayerDto> entityModel = playerAssembler.toModel(player);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }


    // FIXME multi-parameter cannot indicate
    /**
     * dynamic search method
     */
    @ApiOperation(value = "선수 검색", notes = "이름/팀/포지션을 동적으로 설정하여 선수를 검색합니다")
    @GetMapping("/players/search")
    public CollectionModel<EntityModel<PlayerDto>> search(
            @RequestParam Map<String, String> param) {

        String name = param.get("name");
        String team = param.get("team");
        String position = param.get("position");

        Long teamId = null;
        if (team != null) teamId = Long.parseLong(team);

        List<EntityModel<PlayerDto>> players = playerService.findPlayer(name, teamId, position).stream()
                .map(playerAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(players, linkTo(methodOn(PlayerController.class).search(param)).withSelfRel());
    }

    /**
     * delete player
     */
    @ApiOperation(value = "선수 삭제", notes ="입력 받은 id에 해당하는 선수를 삭제합니다.")
    @DeleteMapping("/players/{id}")
    public void delete(
            @ApiParam(value = "삭제할 선수의 아이디", required = true) @PathVariable Long id) {
        playerService.deletePlayer(id);
    }

}
