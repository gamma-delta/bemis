package at.petrak.bemis.api;

import at.petrak.bemis.api.book.BemisBookPath;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * What lies at the end of a path.
 * <p>
 * Either {@code item} or {@code children} (or both) must be present. The both case is if you have something like:
 * <pre>
 * path/to/folder/
 * - foo.xml
 * - foo/
 *   - bar.xml
 *   - baz.xml
 * </pre>
 * <p>
 * with a folder and file of the same name {@code foo}. If neither were present then there wouldn't be any point in
 * keeping
 * the entry around in memory.
 */
public final class IndexTreeNode<T> {
    private final Optional<T> item;
    private @Nullable Map<String, IndexTreeNode<T>> children;

    public IndexTreeNode(@Nullable T item, @Nullable Map<String, IndexTreeNode<T>> children) {
        this(Optional.ofNullable(item), children);
    }

    private IndexTreeNode(Optional<T> item, @Nullable Map<String, IndexTreeNode<T>> children) {
        this.item = item;
        this.children = children;
    }

    public void assertBothNonNull() {
        Preconditions.checkArgument(this.item.isPresent() || this.children != null,
            "entry can't have no item or children");
    }

    /**
     * Assuming this is the root node, create a new Entry with the given item at the given path.
     * <p>
     * Returns the new entry.
     */
    public IndexTreeNode<T> insertAt(T item, BemisBookPath path) {
        if (path.size() == 1) {
            // Then this is the last segment! yahoo
            if (this.children == null) {
                this.children = new HashMap<>();
            }
            var newEntry = new IndexTreeNode<>(item, null);
            this.children.put(path.get(0), newEntry);
            return newEntry;
        } else {
            // recurse
            if (this.children == null) {
                this.children = new HashMap<>();
            }

            var subentry = new IndexTreeNode<T>(Optional.empty(), new HashMap<>());
            var out = subentry.insertAt(item, path.popFront());
            this.children.put(path.get(0), subentry);
            return out;
        }
    }

    public @Nullable IndexTreeNode<T> get(BemisBookPath path) {
        if (this.children == null)
            return null;

        var kid = this.children.get(path.get(0));
        if (path.size() == 1) {
            // then we're here
            // may be null, that's ok
            return kid;
        } else {
            return kid.get(path.popFront());
        }
    }

    public <U> IndexTreeNode<U> map(Function<T, U> mapper) {
        var item2 = this.item.map(mapper);
        Map<String, IndexTreeNode<U>> children2 = null;
        if (this.children != null) {
            children2 = new HashMap<>();
            for (var entry : this.children.entrySet()) {
                children2.put(entry.getKey(), entry.getValue().map(mapper));
            }
        }
        return new IndexTreeNode<>(item2, children2);
    }

    public Optional<T> item() {
        return item;
    }

    public @Nullable Map<String, IndexTreeNode<T>> children() {
        return children;
    }

    @Override
    public String toString() {
        return "Entry[" +
            "item=" + item + ", " +
            "children=" + children + ']';
    }
}
