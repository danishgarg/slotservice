package dg.athena.sideprojects.SlotService.Domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Aggregate{
    @JsonIgnore
    public List<DomainEvent> getDirtyEvents();
}