package jangseop.myleague.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jangseop.myleague.assembler.LeagueAssembler;
import jangseop.myleague.domain.League;
import jangseop.myleague.dto.LeagueDto;
import jangseop.myleague.dto.PlayerDto;
import jangseop.myleague.repository.LeagueRepository;
import jangseop.myleague.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Api(tags = {"3. League"})
@RestController
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;
    private final LeagueRepository leagueRepository;
    private final LeagueAssembler leagueAssembler;

    @ApiOperation(value = "리그 전체 조회", notes = "데이터베이스에 저장된 모든 리그를 조회합니다.")
    @GetMapping("/leagues")
    public CollectionModel<EntityModel<LeagueDto>> all() {
        List<EntityModel<LeagueDto>> leagues = leagueRepository.findAll().stream()
                .map(leagueAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(leagues,
                linkTo(methodOn(LeagueController.class).all()).withSelfRel());
    }

    @ApiOperation(value = "리그 단건 조회", notes = "입력 받은 아이디에 해당하는 리그를 조회합니다.")
    @GetMapping("/leagues/{id}")
    public EntityModel<LeagueDto> one(
            @ApiParam(value = "리그 아이디", required = true) @PathVariable Long id) {

        League league = leagueRepository.findOne(id);

        return leagueAssembler.toModel(league);
    }

    @ApiOperation(value = "리그 생성", notes = "새로운 리그를 생성하여 데이버베이스에 저장합니다.")
    @PostMapping("/leagues")
    public EntityModel<LeagueDto> newLeague(
            @ApiParam(value = "생성할 리그 정보") @RequestBody LeagueDto leagueDto) {
        League league = leagueService.create(leagueDto);
        return leagueAssembler.toModel(league);
    }
}
