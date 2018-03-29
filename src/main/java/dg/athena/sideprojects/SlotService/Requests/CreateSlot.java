package dg.athena.sideprojects.SlotService.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSlot{

    private String start;
    private String end;
    private String providerId;
}