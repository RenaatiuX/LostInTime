package com.ren.lostintime.client.screen.book; // ¡Ajusta tu package si es necesario!

import com.mojang.blaze3d.platform.Lighting;
import com.ren.lostintime.LostInTime;
import com.ren.lostintime.common.entity.creatures.Helicoprion;
import com.ren.lostintime.common.entity.creatures.Mastodonsaurus;
import com.ren.lostintime.common.init.EntityInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class PrehistoricBookScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(LostInTime.MODID, "textures/gui/prehistoric_book.png");

    private final int imageWidth = 256;
    private final int imageHeight = 192;

    private int currentPage = 0;
    private int maxPages = 2;

    private PageButton btnNext;
    private PageButton btnPrev;
    private ItemStack hoveredItem = ItemStack.EMPTY;

    private final List<CreaturePage> creaturePages = new ArrayList<>();

    public PrehistoricBookScreen() {
        super(Component.translatable("gui.lostintime.prehistoric_book"));
        this.creaturePages.add(new CreaturePage("Helicoprion", EntityInit.HELICOPRION.get(), new ItemStack(Items.SLIME_BALL), "Tiburón de sierra"));
        this.creaturePages.add(new CreaturePage("Mastodonsaurus", EntityInit.MASTODONSAURUS.get(), new ItemStack(Items.COD), "Anfibio masivo"));

        this.maxPages = 2 + this.creaturePages.size();
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        this.btnPrev = new PageButton(x + 20, y + 155, false, (button) -> {
            if (this.currentPage > 0) {
                this.currentPage--;
                this.updateButtons();
            }
        }, true);

        this.btnNext = new PageButton(x + 215, y + 155, true, (button) -> {
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
            if (isMouseOver(pMouseX, pMouseY, x + 30, y + 45, 70, 10)) {
                this.currentPage = 3;
                this.updateButtons();
                playClickSound();
                return true;
            }
            if (isMouseOver(pMouseX, pMouseY, x + 30, y + 60, 90, 10)) {
                this.currentPage = 4;
                this.updateButtons();
                playClickSound();
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        pGuiGraphics.blit(BOOK_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
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

            for (int i = 0; i < this.creaturePages.size(); i++) {
                CreaturePage page = this.creaturePages.get(i);
                int textY = y + 45 + (i * 15); // Separamos cada línea por 15 pixeles
                boolean isHovering = isMouseOver(pMouseX, pMouseY, x + 30, textY, 100, 10);

                pGuiGraphics.drawString(this.font, "- " + page.name, x + 30, textY, isHovering ? 0x0000FF : 0x8B0000, false);
            }
        }
        else {
            int pageIndex = this.currentPage - 3;
            if (pageIndex >= 0 && pageIndex < this.creaturePages.size()) {
                CreaturePage page = this.creaturePages.get(pageIndex);
                LivingEntity entity = page.getEntityType(Minecraft.getInstance().level);

                if (entity != null) {
                    // Llamamos a tu método maestro (asegúrate de tenerlo en la clase)
                    renderCreatureLayout(pGuiGraphics, pMouseX, pMouseY, x, y, page.name, entity, page.drop, page.description);
                }
            }
        }

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (!this.hoveredItem.isEmpty()) {
            pGuiGraphics.renderTooltip(this.font, this.hoveredItem, pMouseX, pMouseY);
        }
    }

    private void renderCreatureLayout(GuiGraphics graphics, int mouseX, int mouseY, int x, int y,
                                      String name, LivingEntity entity, ItemStack drop, String description) {

        graphics.drawCenteredString(this.font, name, x + 64, y + 15, 0x8B7D6B);
        graphics.fill(x + 20, y + 26, x + 108, y + 27, 0x558B7D6B);

        int entityRenderX = x + 64;
        int entityRenderY = y + 110;
        float lookX = (float)entityRenderX - mouseX;
        float lookY = (float)(entityRenderY - 30) - mouseY;

        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, entityRenderX, entityRenderY, 20, lookX, lookY, entity);
        Lighting.setupFor3DItems();

        graphics.drawString(this.font, Component.literal("Drops:"), x + 25, y + 140, 0x555555, false);
        graphics.renderItem(drop, x + 65, y + 137);

        if (isMouseOver(mouseX, mouseY, x + 65, y + 137, 16, 16)) {
            this.hoveredItem = drop;
        }

        graphics.drawString(this.font, description, x + 140, y + 35, 0x555555, false);

    }
}