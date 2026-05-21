package ulb.common.dto.display;

/** Grid coordinates of an edge between two rooms. */
public record ConnectionDisplayDTO(int fromRow, int fromCol, int toRow, int toCol) {
}
