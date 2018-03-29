package dg.athena.sideprojects.SlotService.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloseSlot{
    private String closedBy;
    private String reason;
}