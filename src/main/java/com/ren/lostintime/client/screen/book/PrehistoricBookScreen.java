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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class PrehistoricBookScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/prehistoric_book.png");

    private final int imageWidth = 328;
    private final int imageHeight = 181;

    private final int xMarginToCenter = 20;

    private int topPos, topPagePos;
    private int leftPos, leftPagePos;

    private int currentPage = 0;
    private int maxPages = 2;

    private PageButton btnNext;
    private PageButton btnPrev;
    private ItemStack hoveredItem = ItemStack.EMPTY;

    private final List<Page> pages = new ArrayList<>();

    public PrehistoricBookScreen() {
        super(Component.translatable("gui."+ LostInTime.MODID + ".prehistoric_book"));
        this.pages.add(new CreaturePage("Helicoprion", EntityInit.HELICOPRION.get(), new ItemStack(Items.SLIME_BALL), "Tiburón de sierra"));
        this.pages.add(new CreaturePage("Mastodonsaurus", EntityInit.MASTODONSAURUS.get(), new ItemStack(Items.COD), "Anfibio masivo"));

        this.maxPages = 2 + this.pages.size();
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;

        //switching to relative number basically, so what we are doing here is setting a margin of 5 % of the image width and height
        this.btnPrev = new PageButton(leftPos + xMarginToCenter + Math.round((imageWidth - 2f * xMarginToCenter) * 0.05f), topPos + Math.round(imageHeight * 0.95f) - 13, false, (button) -> {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.updateButtons();
            }
        }, true);

        this.btnNext = new PageButton(leftPos + xMarginToCenter + Math.round((imageWidth - 2f * xMarginToCenter) * 0.95f) - 23, topPos + Math.round(imageHeight * 0.95f) - 13, true, (button) -> {
            if (this.currentPage < this.maxPages) {
                this.currentPage++;
                this.updateButtons();
            }
        }, true);

        this.addRenderableWidget(this.btnPrev);
        this.addRenderableWidget(this.btnNext);
        this.updateButtons();
    }

    private void updateButtons() {
        this.btnPrev.visible = this.currentPage > 0;
        this.btnNext.visible = this.currentPage < this.maxPages;
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
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (this.currentPage == 2 && pButton == 0) {
            for (int i = 0; i < this.pages.size(); i++) {
                int textY = y + 45 + (i * 15);
                if (isMouseOver(pMouseX, pMouseY, x + 30, textY, 100, 10)) {
                    this.currentPage = 3 + i;
                    this.updateButtons();
                    playClickSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    public void setHoveredItem(ItemStack stack) {
        this.hoveredItem = stack;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        pGuiGraphics.blit(BOOK_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 512, 512);
        this.hoveredItem = ItemStack.EMPTY;

        if (this.currentPage == 0) {
            pGuiGraphics.drawCenteredString(this.font, "LOST IN TIME", this.width / 2, y + 60, 0x8B0000);
            pGuiGraphics.drawCenteredString(this.font, "Diario de Expedición", this.width / 2, y + 80, 0x555555);
        }
        else if (this.currentPage == 1) {
            pGuiGraphics.drawString(this.font, "Introducción", x + 30, y + 20, 0x000000, false);
            pGuiGraphics.drawString(this.font, "Hace millones de años...", x + 30, y + 45, 0x333333, false);
        }
        else if (this.currentPage == 2) {
            pGuiGraphics.drawString(this.font, "Índice de Especies", x + 30, y + 20, 0x000000, false);

            for (int i = 0; i < this.pages.size(); i++) {
                Page page = this.pages.get(i);
                int textY = y + 45 + (i * 15);
                boolean isHovering = isMouseOver(pMouseX, pMouseY, x + 30, textY, 100, 10);

                pGuiGraphics.drawString(this.font, "- " + page.name, x + 30, textY, isHovering ? 0x0000FF : 0x8B0000, false);
            }
        }
        else {
            int pageIndex = this.currentPage - 3;
            if (pageIndex >= 0 && pageIndex < this.pages.size()) {
                Page page = this.pages.get(pageIndex);
                page.render(pGuiGraphics, pMouseX, pMouseY, x, y, this.font, this);
            }
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (!this.hoveredItem.isEmpty()) {
            pGuiGraphics.renderTooltip(this.font, this.hoveredItem, pMouseX, pMouseY);
        }
    }
}
