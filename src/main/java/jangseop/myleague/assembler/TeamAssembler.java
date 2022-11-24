package jangseop.myleague.assembler;

import jangseop.myleague.controller.ParticipantController;
import jangseop.myleague.controller.PlayerController;
import jangseop.myleague.controller.TeamController;
import jangseop.myleague.domain.Team;
import jangseop.myleague.dto.TeamDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TeamAssembler implements RepresentationModelAssembler<Team, EntityModel<TeamDto>> {

    @Override
    public EntityModel<TeamDto> toModel(Team team) {

        TeamDto teamDto = new TeamDto(team.getId(), team.getName(), team.getShortName(), team.getTeamStat(), team.getActivation());

        Map<String, String> param = new HashMap<>();
        if (team.getId() != null) {
            param.put("team", team.getId().toString());
        }

        return EntityModel.of(teamDto,
                linkTo(methodOn(TeamController.class).one(team.getId())).withSelfRel(),
                linkTo(methodOn(TeamController.class).all()).withRel("teams"),
                linkTo(methodOn(ParticipantController.class).search(param)).withRel("records"),
                linkTo(methodOn(PlayerController.class).search(param)).withRel("players")
        );
    }
}
