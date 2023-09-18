package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.Data;

@Data
public class PatchOp {
    private String op;
    private String path;
    private String value;

    public PatchOp(String op, String path, String value) {
        this(op, path);
        this.value = value;
    }

    public PatchOp(String op, String path) {
        this.op = op;
        this.path = path.startsWith("/") ? path : "/" + path;
    }

    public static PatchOp replacePatch(String path, String value) {
        return new PatchOp("replace", path, value);
    }

    public static PatchOp removePatch(String path) {
        return new PatchOp("remove", path);
    }
}
