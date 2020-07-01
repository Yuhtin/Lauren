package com.yuhtin.lauren.utils.messages;

import com.google.common.base.Strings;

public class AsciiBox {

    private int size;

    private Boolean fix = false;

    private String inside = " ";

    private String[] borders = new String[4];
    private String[] corners = new String[0];

    public String render(String[] content) {

        boolean haveCorners = this.corners.length > 0;

        String verticalBorder = this.borders[0];

        String border = Strings.repeat(verticalBorder, haveCorners ? this.size - 2 : this.size);

        StringBuilder message = new StringBuilder("\n" + (haveCorners ? this.corners[0] + border + this.corners[1] : border));

        for (String contentItem : content) {
            message.append("\n").append(centerString(contentItem));
        }

        message.append("\n").append(haveCorners ? this.corners[2] + border + this.corners[3] : border);

        return message.toString();
    }

    public AsciiBox border(String border) {
        this.borders[0] = border;
        return this;
    }

    public AsciiBox borders(String vertical, String horizontal) {
        this.borders = new String[]{vertical, horizontal};
        return this;
    }

    public AsciiBox corner(String corner) {
        this.corners = new String[]{corner, corner, corner, corner};
        return this;
    }

    public AsciiBox corners(String topLeft, String topRight, String bottomLeft, String bottomRight) {
        this.corners = new String[]{topLeft, topRight, bottomLeft, bottomRight};
        return this;
    }

    public AsciiBox size(int size) {
        this.size = size;
        return this;
    }

    public AsciiBox inside(String inside) {
        this.inside = inside;
        return this;
    }

    public AsciiBox fixSide(Boolean side) {
        this.fix = side;
        return this;
    }

    private String centerString(String string) {

        int stringLenght = string.length();

        int calc = ((this.size - stringLenght) / 2) - 1;

        String space = Strings.repeat(this.inside, calc);

        String leftSpace = space;
        String rightSpace = space;

        String horizontalBorder = this.borders.length == 2 ? this.borders[1] : this.borders[0];

        if ((stringLenght % 2) != 0) {
            if (this.fix) {
                leftSpace += Strings.repeat(this.inside, 1);
            } else {
                rightSpace += Strings.repeat(this.inside, 1);
            }
        }

        return horizontalBorder + leftSpace + string + rightSpace + horizontalBorder;
    }

}
