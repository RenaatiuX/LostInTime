package com.ren.lostintime.client.screen.book.components;

import com.ren.lostintime.client.screen.book.PrehistoricBookScreen;

public class ForwardButton extends ImageComponent {

    protected OneDimensionalNavigatableComponent navigator;

    public ForwardButton(OneDimensionalNavigatableComponent navigator) {
        super(PrehistoricBookScreen.BOOK_TEXTURE, 306, 5, 5, 8, 512, 512, true);
        this.navigator = navigator;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button) {
        navigator.forward();
        return true;
    }
}
