package jangseop.myleague.assembler;

import jangseop.myleague.controller.PlayerController;
import jangseop.myleague.domain.Player;
import jangseop.myleague.dto.PlayerDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlayerAssembler implements RepresentationModelAssembler<Player, EntityModel<PlayerDto>> {

    @Override
    public EntityModel<PlayerDto> toModel(Player player) {

        PlayerDto playerDto = new PlayerDto(player.getId(), player.getName(), player.getPosition(), player.getStat(), player.getTeam());

        return EntityModel.of(playerDto,
                linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel(),
                linkTo(methodOn(PlayerController.class).all()).withRel("players"));

    }
}
