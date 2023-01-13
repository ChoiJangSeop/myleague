package jangseop.myleague.assembler;

import jangseop.myleague.controller.ParticipantController;
import jangseop.myleague.domain.record.Record;
import jangseop.myleague.dto.RecordDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RecordAssembler implements RepresentationModelAssembler<Record, EntityModel<RecordDto>> {
    @Override
    public EntityModel<RecordDto> toModel(Record record) {

        RecordDto recordDto = new RecordDto(
                record.getId(),
                record.getRound(),
                record.getParticipant().getId(),
                record.getType(),
                record.getWin(),
                record.getDraw(),
                record.getLoss(),
                record.getSetWin(),
                record.getSetLoss(),
                record.getScore(),
                record.getRank(),
                record.getPromotion());

        return EntityModel.of(recordDto,
                linkTo(methodOn(ParticipantController.class).oneRecord(record.getParticipant().getId(), record.getRound())).withSelfRel(),
                linkTo(methodOn(ParticipantController.class).allRecords(record.getParticipant().getId())).withRel("records"));
    }
}
