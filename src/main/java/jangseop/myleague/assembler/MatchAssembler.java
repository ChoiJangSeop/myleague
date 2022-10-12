package jangseop.myleague.assembler;

import jangseop.myleague.controller.MatchController;
import jangseop.myleague.domain.Match;
import jangseop.myleague.dto.MatchDto;
import jangseop.myleague.service.MatchService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MatchAssembler implements RepresentationModelAssembler<Match, EntityModel<MatchDto>> {


    @Override
    public EntityModel<MatchDto> toModel(Match match) {
        MatchDto dto = new MatchDto(match);

        return EntityModel.of(dto,
                linkTo(methodOn(MatchController.class).one(match.getId())).withSelfRel(),
                linkTo(methodOn(MatchController.class).all()).withRel("matches"));
    }
}
