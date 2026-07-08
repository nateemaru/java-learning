import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomStringBuilder {
    private final List<StringBuilderSnapshot> snapshots;
    private static final byte[] NULL_BYTES = {'n', 'u', 'l', 'l'};

    public CustomStringBuilder() {
        this.snapshots = new ArrayList<>();
        this.snapshots.add(new StringBuilderSnapshot(new byte[0]));
    }

    public CustomStringBuilder append(Object obj) {
        if (obj == null) {
            return addNull();
        }
        return append(String.valueOf(obj));
    }

    public CustomStringBuilder append(String str) {
        if (str == null) {
            return addNull();
        }

        addNewBytes(str);
        return this;
    }

    public CustomStringBuilder undo() {
        if (snapshots.size() > 1) {
            snapshots.removeLast();
        }

        return this;
    }

    @Override
    public String toString() {
        return new String(snapshots.getLast().value(), StandardCharsets.UTF_8);
    }

    private void addNewBytes(String str) {
        byte[] newStrAsBytes = str.getBytes(StandardCharsets.UTF_8);
        saveNewSnapshot(newStrAsBytes);
    }

    private CustomStringBuilder addNull() {
        saveNewSnapshot(CustomStringBuilder.NULL_BYTES);
        return this;
    }

    private void saveNewSnapshot(byte[] newBytes) {
        byte[] lastSnapshotValue = snapshots.getLast().value();
        byte[] result = new byte[newBytes.length + lastSnapshotValue.length];
        System.arraycopy(lastSnapshotValue, 0, result, 0, lastSnapshotValue.length);
        System.arraycopy(newBytes, 0, result, lastSnapshotValue.length, newBytes.length);
        snapshots.add(new StringBuilderSnapshot(result));
    }

    private record StringBuilderSnapshot(byte[] value) {
        private StringBuilderSnapshot {
            value = Arrays.copyOf(value, value.length);
        }

        @Override
        public byte[] value() {
            return Arrays.copyOf(value, value.length);
        }
    }
}
