package at.petrak.bemis.api.book;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The unique path to a page in a {@link IBemisBook}.
 * <p>
 * Logically, this is a sequence of at least one <strong>segment</strong>, separated by slashes.
 * The segments are limited to lowercase characters, digits, dashes, underscores, and periods.
 * <p>
 * Note that there is *no* {@code .xml} suffix on any of these. Also note that a {@link BemisBookPath} means
 * nothing not in the context of a given book.
 */
@Immutable
public final class BemisBookPath {
    private final List<String> segments;

    public BemisBookPath(String first, String... more) throws IllegalArgumentException {
        if (!isValidSegment(first)) {
            throw new IllegalArgumentException("first string " + first + " had an invalid char");
        }

        this.segments = new ArrayList<>(more.length + 1);
        this.segments.add(first);
        for (int i = 0; i < more.length; i++) {
            String m = more[i];
            if (!isValidSegment(m)) {
                throw new IllegalArgumentException("string at idx " + (i + 2) + ", " + m + " had an invalid char");
            }
            this.segments.add(m);
        }
    }

    /**
     * Does no validation
     */
    private BemisBookPath(List<String> segments) {
        this.segments = segments;
    }

    /**
     * Return the number of segments in the path.
     */
    public int size() {
        return this.segments.size();
    }

    /**
     * Return the segment at the i'th position.
     * <p>
     * Negative values are accepted; -1 means the last, and so on.
     */
    public String get(int i) throws IndexOutOfBoundsException {
        if (i < 0) {
            if ((-i) > this.segments.size()) {
                throw new IndexOutOfBoundsException(
                    "negative index " + i + " was out of range, should be " + (-this.segments.size()) + " or lesser");
            }
            return this.segments.get(this.segments.size() - i);
        } else {
            return this.segments.get(i);
        }
    }

    /**
     * Return a copy of this with the new string(s) as new segments.
     */
    public BemisBookPath append(String s, String... more) throws IllegalArgumentException {
        var other = new BemisBookPath(s, more);
        return this.join(other);
    }

    /**
     * Return a copy of this with {@code other}'s segments appended at the end of this one.
     */
    public BemisBookPath join(BemisBookPath other) {
        var out = new ArrayList<String>(this.segments.size() + other.segments.size());
        out.addAll(this.segments);
        out.addAll(other.segments);
        return new BemisBookPath(out);
    }

    /**
     * Return a copy of this with i segments popped off the end.
     *
     * @throws IllegalArgumentException if i is less than 0 or large enough the result would be empty.
     */
    public BemisBookPath popN(int i) throws IllegalArgumentException {
        // We return the same obj, but because this class is immutable it doesn't matter
        if (i == 0) {
            return this;
        }
        if (i < 0 || i > this.segments.size() - 1) {
            throw new IllegalArgumentException("popcount " + i + "out of bounds");
        }
        return new BemisBookPath(this.segments.subList(0, this.segments.size() - i));
    }

    /**
     * Return a copy with the last element popped off
     */
    public BemisBookPath pop() throws IllegalArgumentException {
        return this.popN(1);
    }

    /**
     * Return a copy of this with the first segment removed.
     */
    public BemisBookPath popFront() {
        if (this.segments.size() <= 1) {
            throw new IllegalStateException("tried to pop the front into an empty path");
        }
        return new BemisBookPath(this.segments.subList(1, this.segments.size()));
    }

    public static boolean isValidSegment(String s) {
        return s.chars().allMatch(i -> isValidSegmentChar((char) i));
    }

    public static boolean isValidSegmentChar(char ch) {
        return 'a' <= ch && ch <= 'z'
            || '0' <= ch && ch <= '9'
            || ch == '-' || ch == '_' || ch == '.';
    }

    @Override
    public String toString() {
        return String.join("/", this.segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BemisBookPath that = (BemisBookPath) o;
        return Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }
}
