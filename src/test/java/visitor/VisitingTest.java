package visitor;

import org.junit.jupiter.api.Test;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static visitor.Visiting.Visitable;
import static visitor.Visiting.Visitor;

public final class VisitingTest {

    @Test
    void itWorks() {
        final TreeNode tree = new BinaryNode(
                new UnaryNode(new Leaf()), new Leaf());
        final var printer = new PrintVisitor();
        printer.dispatch(tree);
        final var result = printer.toString();
        final var expected = "Binary node\nUnary node\nLeaf\nLeaf\n";
        assertEquals(expected, result);
    }

    @Test
    void notVisitable() {
        final String notVisitable = "TreeNode";
        final var printer = new PrintVisitor();
        printer.dispatch(notVisitable);
        final var result = printer.toString();
        final var expected = "Unknown\n";
        assertEquals(expected, result);
    }

    private static abstract class TreeNode implements Visitable {
    }

    private static final class Leaf extends TreeNode {
        @Override
        public void accept(final Visitor ignored) {
            // Do nothing.
        }
    }

    private static final class UnaryNode extends TreeNode {
        private final TreeNode child;

        private UnaryNode(final TreeNode child) {
            this.child = requireNonNull(child);
        }

        @Override
        public void accept(final Visitor visitor) {
            visitor.dispatch(child);
        }
    }

    private static final class BinaryNode extends TreeNode {
        private final TreeNode left;
        private final TreeNode right;

        private BinaryNode(final TreeNode left, final TreeNode right) {
            this.left = requireNonNull(left);
            this.right = requireNonNull(right);
        }

        @Override
        public void accept(final Visitor visitor) {
            visitor.dispatch(left);
            visitor.dispatch(right);
        }
    }

    private static final class PrintVisitor implements Visitor {
        private final StringBuilder builder = new StringBuilder();

        private void print(final String message) {
            builder.append(message);
            builder.append('\n');
        }

        @Override
        public void other(final Object o) {
            print("Unknown");
        }

        @SuppressWarnings("unused")
        public void visit(final Leaf leaf) {
            print("Leaf");
        }


        @SuppressWarnings("unused")
        public void visit(final UnaryNode binaryNode) {
            print("Unary node");
        }

        @SuppressWarnings("unused")
        public void visit(final BinaryNode binaryNode) {
            print("Binary node");
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
