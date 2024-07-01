package myregex;

import java.util.LinkedList;

class AST {

    private final Node root;

    public AST(String regularExpression) {
        Element current = convertToElements("(" + regularExpression + ")");

        LinkedList<Element> stack = new LinkedList<>();
        while ((current.getLeft() != null) || (current.getType() == Type.OPEN)) {
            if (current.getType() == Type.CLOSE) {
                stack.add(current);
            }

            if (current.getType() == Type.OPEN) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException(
                            "!!!Syntax error: count open parenthesis more than count close parenthesis!!!");
                }
                Element end = stack.getLast();
                stack.removeLast();

                boolean isEmpty = (current.getRight() == end);
                if (isEmpty) {
                    boolean leftIsNull = (current.getLeft() == null);
                    boolean rightIsNull = (end.getRight() == null);

                    if (leftIsNull && rightIsNull) {

                        current.setRight(null);
                        end.setLeft(null);

                        current = new Element(Type.EPSILON, new Node(Type.EPSILON, null));
                        break;
                    }

                    if (leftIsNull) {
                        throw new IllegalArgumentException(
                                "!!!Syntax error: count close parenthesis more than count open parenthesis!!!");
                    }

                    if (rightIsNull) {
                        throw new IllegalArgumentException(
                                "!!!Syntax error: count open parenthesis more than count close parenthesis!!!");
                    }

                    Element previousLeft = current.getLeft();
                    Element nextRight = end.getRight();

                    current.setLeft(null);
                    current.setRight(null);
                    end.setLeft(null);
                    end.setRight(null);

                    previousLeft.setRight(nextRight);
                    nextRight.setLeft(previousLeft);

                    current = previousLeft;
                    continue;
                }

                symbolStage(current, end);
                leftUnaryStage(current, end, Type.PLUS);
                leftUnaryStage(current, end, Type.REPEAT);
                binaryStage(current, end, Type.DOT);
                dotStage(current, end);
                binaryStage(current, end, Type.PIPE);
                rightUnaryStage(current, end, Type.GROUP);

                Element result = current.getRight();

                boolean isSyntaxError = (result.getNode() == null) ||
                        (current.getRight() != end.getLeft());
                if (isSyntaxError) {
                    throw new IllegalArgumentException("!!!Syntax error!!!");
                }

                boolean leftIsNull = (current.getLeft() == null);
                boolean rightIsNull = (end.getRight() == null);

                if (leftIsNull && rightIsNull) {

                    current.setRight(null);
                    end.setLeft(null);

                    result.setLeft(null);
                    result.setRight(null);

                    current = result;
                    continue;
                }

                if (leftIsNull) {
                    throw new IllegalArgumentException(
                            "!!!Syntax error: count close parenthesis more than count open parenthesis!!!");
                }

                if (rightIsNull) {
                    throw new IllegalArgumentException(
                            "!!!Syntax error: count open parenthesis more than count close parenthesis!!!");
                }

                Element previousLeft = current.getLeft();
                Element nextRight = end.getRight();

                current.setLeft(null);
                current.setRight(null);
                end.setLeft(null);
                end.setRight(null);

                previousLeft.setRight(result);
                result.setLeft(previousLeft);

                nextRight.setLeft(result);
                result.setRight(nextRight);

                current = result;

            }

            current = current.getLeft();
        }

        if (current.getNode() == null) {
            root = new Node(Type.EPSILON, null);
        } else {
            root = current.getNode();
        }

    }

    private void symbolStage(Element start, Element end) {
        Element current = start.getRight();
        while (current != end) {
            boolean isSymbolOREpsilon = (current.getType() == Type.SYMBOL) ||
                    (current.getType() == Type.EPSILON);
            if (isSymbolOREpsilon) {
                current.setNode(new Node(current.getType(), current.getData()));
            }
            current = current.getRight();
        }
    }

    private void leftUnaryStage(Element start, Element end, Type type) {
        Element current = start.getRight();
        while (current != end) {
            boolean isType = ((current.getNode() == null) &&
                    (current.getType() == type));
            boolean leftIsNode =
                    (current.getLeft().getNode() != null);
            boolean isNodeType = isType && leftIsNode;
            if (isNodeType) {
                Element left = current.getLeft();
                Element previous = left.getLeft();

                left.setLeft(null);
                left.setRight(null);

                previous.setRight(current);
                current.setLeft(previous);

                Node node = new Node(current.getType(), current.getData());
                node.getChildren().add(left.getNode());
                left.setNode(null);

                current.setNode(node);

            }

            current = current.getRight();
        }
    }

    private void rightUnaryStage(Element start, Element end, Type type) {
        Element current = start.getRight();
        while (current != end) {
            boolean isType = ((current.getNode() == null) &&
                    (current.getType() == type));
            boolean rightIsNode =
                    (current.getRight().getNode() != null);
            boolean isTypeNode = isType && rightIsNode;
            if (isTypeNode) {
                Element right = current.getRight();
                Element next = right.getRight();

                right.setLeft(null);
                right.setRight(null);

                next.setLeft(current);
                current.setRight(next);

                Node node = new Node(current.getType(), current.getData());
                node.getChildren().add(right.getNode());
                right.setNode(null);

                current.setNode(node);
            }

            current = current.getRight();
        }
    }

    private void dotStage(Element start, Element end) {
        Element current = start.getRight();
        while (current != end) {
            boolean isNode = (current.getNode() != null);
            boolean leftIsNode =
                    (current.getLeft().getNode() != null);
            boolean isNodeNode = isNode && leftIsNode;
            if (isNodeNode) {
                Element left = current.getLeft();
                Element previousLeft = left.getLeft();
                Element right = current.getRight();

                left.setLeft(null);
                left.setRight(null);
                current.setLeft(null);
                current.setRight(null);

                Element element = new Element(Type.DOT, null);
                element.setRight(right);
                element.setLeft(previousLeft);

                previousLeft.setRight(element);
                right.setLeft(element);

                Node node = new Node(Type.DOT, null);
                node.getChildren().add(left.getNode());
                node.getChildren().add(current.getNode());
                left.setNode(null);
                current.setNode(null);

                element.setNode(node);
                current = element;
            }

            current = current.getRight();
        }
    }

    private void binaryStage(Element start, Element end, Type type) {
        Element current = start.getRight();
        while (current != end) {
            boolean isType = ((current.getNode() == null) &&
                    (current.getType() == type));
            boolean leftIsNode =
                    (current.getLeft().getNode() != null);
            boolean rightIsNode =
                    (current.getRight().getNode() != null);
            boolean isNodeTypeNode = isType && leftIsNode && rightIsNode;
            if (isNodeTypeNode) {
                Element left = current.getLeft();
                Element previousLeft = left.getLeft();
                Element right = current.getRight();
                Element nextRight = right.getRight();

                left.setLeft(null);
                left.setRight(null);
                right.setLeft(null);
                right.setRight(null);

                previousLeft.setRight(current);
                current.setLeft(previousLeft);

                nextRight.setLeft(current);
                current.setRight(nextRight);

                Node node = new Node(current.getType(), current.getData());
                node.getChildren().add(left.getNode());
                node.getChildren().add(right.getNode());
                left.setNode(null);
                right.setNode(null);

                current.setNode(node);
            }

            current = current.getRight();
        }
    }

    private Element convertToElements(String input) {
        Element current = null;
        int length = input.length();
        for (int iterator = 0; iterator < length; iterator++) {
            char symbol = input.charAt(iterator);
            Object data = null;
            Type type;
            switch (symbol) {
                case '#':
                    iterator++;
                    data = input.charAt(iterator);
                    type = Type.SYMBOL;
                    break;
                case '^':
                    type = Type.EPSILON;
                    break;
                case '|':
                    type = Type.PIPE;
                    break;
                case '.':
                    type = Type.DOT;
                    break;
                case '+':
                    type = Type.PLUS;
                    break;
                case '{':
                    type = Type.REPEAT;
                    {
                        Pair<Integer, Object> result = calculateDataForRepeat(input, ++iterator);
                        iterator = result.first();
                        data = result.second();
                    }
                    break;
                case ':':
                    type = Type.GROUP;
                    {
                        Pair<Element, Integer> result = calculateDataForGroup(current);
                        current = result.first();
                        data = result.second();
                    }
                    break;
                case '(':
                    type = Type.OPEN;
                    break;
                case ')':
                    type = Type.CLOSE;
                    break;
                default:
                    type = Type.SYMBOL;
                    data = symbol;
                    break;
            }

            Element next = new Element(type, data);
            if (current != null) {
                current.setRight(next);
                next.setLeft(current);
            }
            current = next;

        }

        return current;
    }

    private Pair<Integer, Object> calculateDataForRepeat(String input, int index) {
        int length = input.length();

        if (index == length) {
            throw new IllegalArgumentException("!!!Syntax error. Correct expression: '{x,y}' or '{x,}'!!!");
        }

        StringBuilder first = new StringBuilder();
        char symbol = 0;
        for(; index < length; index++) {
            symbol = input.charAt(index);
            if (symbol == ',') {
                break;
            }

            first.append(symbol);

        }

        if (index == length) {
            throw new IllegalArgumentException("!!!Syntax error. Correct expression: '{x,y}' or '{x,}'!!!");
        }

        if (first.isEmpty()) {
            throw new IllegalArgumentException("!!!Syntax error: x is empty!!!");
        }

        int firstNumber;
        try {
            firstNumber = Integer.parseUnsignedInt(first.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("!!!Syntax error: x is not unsigned number!!!");
        }

        index++;
        StringBuilder second = new StringBuilder();
        for(; index < length; index++) {
            symbol = input.charAt(index);
            if (symbol == '}') {
                break;
            }

            second.append(symbol);

        }

        if (symbol != '}') {
            throw new IllegalArgumentException("!!!Syntax error. Correct expression: '{x,y}' or '{x,}'!!!");
        }

        Integer secondNumber;
        if (second.isEmpty()) {
            secondNumber = null;
        } else {
            try {
                secondNumber = Integer.parseUnsignedInt(second.toString());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("!!!Syntax error: y is not unsigned number!!!");
            }
        }

        Pair<Integer, Integer> data = new Pair<>(firstNumber, secondNumber);
        return new Pair<>(index, data);
    }

    private Pair<Element, Integer> calculateDataForGroup(Element current) {

        if (current == null) {
            throw new IllegalArgumentException("!!!Syntax error: n is empty!!!");
        }

        StringBuilder stringNumber = new StringBuilder();
        while (current.getType() == Type.SYMBOL) {

            stringNumber.append((char)current.getData());

            Element previous = current.getLeft();
            current.setLeft(null);
            current = previous;
            if (previous != null) {
                previous.setRight(null);
            } else {
                break;
            }

        }

        if (stringNumber.isEmpty()) {
            throw new IllegalArgumentException("!!!Syntax error: n is empty!!!");
        }

        int number;
        try {
            number = Integer.parseUnsignedInt(stringNumber.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("!!!Syntax error: n is not unsigned number!!!");
        }

        return new Pair<>(current, number);
    }

    private Object traversNode(Node current, Action action) {
        LinkedList<Object> children = new LinkedList<>();
        for (Node child: current.getChildren()) {
            children.add(traversNode(child, action));
        }
        return action.run(current.getType(), current.getData(), children);
    }

    public Object travers(Action action) {
        return traversNode(root, action);
    }

    public interface Action {
        Object run(Type operation, Object data, LinkedList<Object> children);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    private static class Element {

        private final Type type;
        private final Object data;
        private Node node = null;

        private Element left = null;
        private Element right = null;


        public Element(Type type, Object data) {
            this.type = type;
            this.data = data;
        }

        public Object getData() {
            return data;
        }

        public Type getType() {
            return type;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public Element getLeft() {
            return left;
        }

        public Element getRight() {
            return right;
        }

        public void setLeft(Element left) {
            this.left = left;
        }

        public void setRight(Element right) {
            this.right = right;
        }

    }

    private static class Node {

        private final Type type;
        private final Object data;

        private final LinkedList<Node> children = new LinkedList<>();

        Node (Type type, Object data) {
            this.type = type;
            this.data = data;
        }

        public Type getType() {
            return type;
        }

        public Object getData() {
            return data;
        }

        public LinkedList<Node> getChildren() {
            return children;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(type.toString());

            for (Node node : children) {
                result.append(" ".concat(node.toString()));
            }

            return result.toString();
        }
    }

}
