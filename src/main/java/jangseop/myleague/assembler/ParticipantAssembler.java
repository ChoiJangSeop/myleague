package jangseop.myleague.assembler;

import jangseop.myleague.controller.ParticipantController;
import jangseop.myleague.domain.Participant;
import jangseop.myleague.dto.ParticipantDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ParticipantAssembler implements RepresentationModelAssembler<Participant, EntityModel<ParticipantDto>> {

    @Override
    public EntityModel<ParticipantDto> toModel(Participant participant) {

        ParticipantDto participantDto = new ParticipantDto(
                participant.getId(),
                participant.getTeam().getId(),
                participant.getLeague().getId(),
                participant.getTotalRank());

        return EntityModel.of(participantDto,
                linkTo(methodOn(ParticipantController.class).one(participant.getId())).withSelfRel(),
                linkTo(methodOn(ParticipantController.class).all()).withRel("participants"));
    }

}
