package com.ren.lostintime.client.screen.book;

import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrehistoricBookScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/prehistoric_book.png");

    public static final String TITLE_TRANSLATION_KEY = "gui." + LostInTime.MODID + ".prehistoric_book.title";

    /**
     * those never change depending on the screen instance, so we make them static
     */
    public static final int imageWidth = 283;
    public static final int imageHeight = 181;

    /**
     * as this texture isnt beginning at (0,0) we need to add this margin to draw it correctly
     */
    private static final int xMarginToCenter = 20;

    public static final int pageTurnButtonHeight = 13;
    public static final int pageTurnButtonWidth = 23;

    private int topPos;
    private int leftPos;
    private Rectangle leftPageBounds, rightPageBounds;

    private int arrowBotMargin = 10;
    private float arrowVertMargin = 0.07f;

    //margin in percentage, book turning button height will be already excluded
    private float leftPageLeftMargin = 0.08F;
    private float leftPageRightMargin = 0.03F;
    private float leftPageTopMargin = 0.07F;
    private float leftPageBotMargin = 0.00F;

    private float rightPageLeftMargin = 0.035F;
    private float rightPageRightMargin = 0.08F;
    private float rightPageTopMargin = 0.07F;
    private float rightPageBotMargin = 0.00F;


    private int currentPage = 0;

    private PageButton btnNext;
    private PageButton btnPrev;
    private ItemStack hoveredItem = ItemStack.EMPTY;

    private final List<Page> pages = new ArrayList<>();

    public PrehistoricBookScreen() {
        super(Component.translatable("gui." + LostInTime.MODID + ".prehistoric_book"));
        this.pages.add(new DoublePage(new PrehistoricBookFirstPage(), new IntroductionPage()));
        this.pages.add(new TableOfContentsPage());
        this.pages.add(new DoublePage(new TimePeriodsIndexPage(), new CreatureIndexPage()));
        this.pages.add(new CreaturePage("Helicoprion", EntityInit.HELICOPRION.get(), new ItemStack(Items.SLIME_BALL), "Tiburón de sierra"));
        this.pages.add(new CreaturePage("Mastodonsaurus", EntityInit.MASTODONSAURUS.get(), new ItemStack(Items.COD), "Anfibio masivo"));
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (this.width - imageWidth) / 2;
        topPos = (this.height - imageHeight) / 2;

        int actualPageHeight = imageHeight - arrowBotMargin - pageTurnButtonHeight;
        int actualPageWidth = imageWidth / 2;

        int leftY = topPos + Math.round(actualPageHeight * leftPageTopMargin);//calculating the top margin
        int leftX = leftPos + 1 + Math.round(actualPageWidth * leftPageLeftMargin);
        int leftWidth = Math.round(actualPageWidth * (1 - leftPageLeftMargin - leftPageRightMargin));
        int leftHeight = Math.round(actualPageHeight * (1 - leftPageTopMargin - leftPageBotMargin));
        this.leftPageBounds = new Rectangle(leftX, leftY, leftWidth, leftHeight);

        int rightY = topPos + Math.round(actualPageHeight * rightPageTopMargin);
        int rightX = leftPos + 1 + actualPageWidth + Math.round(actualPageWidth * rightPageLeftMargin);
        int rightWidth = Math.max(1, Math.round(actualPageWidth * (1 - rightPageLeftMargin - rightPageRightMargin)));
        int rightHeight = Math.max(1, Math.round(actualPageHeight * (1 - rightPageTopMargin - rightPageBotMargin)));

        this.rightPageBounds = new Rectangle(rightX, rightY, rightWidth, rightHeight);


        //switching to relative number basically, so what we are doing here is setting a margin of 5 % of the image width and height
        this.btnPrev = new PageButton(leftPos + Math.round(imageWidth * arrowVertMargin), topPos + imageHeight - arrowBotMargin - pageTurnButtonHeight, false, (button) -> previousPage(), false);
        this.btnNext = new PageButton(leftPos + Math.round(imageWidth * (1 - arrowVertMargin)) - pageTurnButtonWidth, topPos + imageHeight - arrowBotMargin - pageTurnButtonHeight, true, (button) -> nextPage(), false);

        this.addRenderableWidget(this.btnPrev);
        this.addRenderableWidget(this.btnNext);
        this.updateButtons();
    }

    private void setPage(int pageNumber) {
        int tmp = this.currentPage;
        this.currentPage = Mth.clamp(pageNumber, 0, this.pages.size() - 1);
        updateButtons();
        if (tmp != this.currentPage) {
            playPageTurnSound();
        }
    }

    private void nextPage() {
        setPage(this.currentPage + 1);
    }

    private void previousPage() {
        setPage(this.currentPage - 1);
    }

    private void updateButtons() {
        this.btnPrev.visible = this.currentPage > 0;
        this.btnNext.visible = this.currentPage < this.pages.size() - 1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        var page = this.pages.get(this.currentPage);
        if (page.isInside((int) pMouseX, (int) pMouseY)) {
            page.onClick((int) pMouseX, (int) pMouseY, pButton);
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void playPageTurnSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    public void setHoveredItem(ItemStack stack) {
        this.hoveredItem = stack;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);

        pGuiGraphics.blit(BOOK_TEXTURE, leftPos, topPos, xMarginToCenter, 0, imageWidth, imageHeight, 512, 512);
        this.hoveredItem = ItemStack.EMPTY;

        var currentPageObject = this.pages.get(this.currentPage);
        if (currentPageObject instanceof DoublePage doublePage){
            doublePage.getLeftPage().setBounds(this.leftPageBounds);
            doublePage.getRightPage().setBounds(this.rightPageBounds);
        }else {
            currentPageObject.setBounds(this.leftPageBounds);
        }
        currentPageObject.render(pGuiGraphics, pMouseX, pMouseY, this.font, this);



        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (!this.hoveredItem.isEmpty()) {
            pGuiGraphics.renderTooltip(this.font, this.hoveredItem, pMouseX, pMouseY);
        }
    }
}
