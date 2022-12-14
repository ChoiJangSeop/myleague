package jangseop.myleague.assembler;

import jangseop.myleague.controller.LeagueController;
import jangseop.myleague.domain.League;
import jangseop.myleague.dto.LeagueDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LeagueAssembler implements RepresentationModelAssembler<League, EntityModel<LeagueDto>> {

    @Override
    public EntityModel<LeagueDto> toModel(League league) {
        LeagueDto leagueDto = new LeagueDto(
                league.getId(),
                league.getTitle(),
                league.getStartedDate() != null ? league.getStartedDate().toString() : null,
                league.getEndDate() != null ? league.getEndDate().toString() : null,
                league.getLeagueStatus(),
                league.getMethod().getRoundrobins(),
                league.getMethod().getPromotion(),
                league.getMethod().getPlayoff());

        return EntityModel.of(leagueDto,
                linkTo(methodOn(LeagueController.class).one(league.getId())).withSelfRel(),
                linkTo(methodOn(LeagueController.class).all()).withRel("leagues"));

    }
}
