package at.petrak.bemis.api.verses.recipe;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.api.book.BemisVerse;
import at.petrak.bemis.api.verses.CenteredDecalVerse;
import at.petrak.bemis.api.verses.ErrorVerse;
import at.petrak.bemis.api.verses.decal.BemisDecal;
import at.petrak.bemis.api.verses.decal.BemisDecalHolder;
import at.petrak.bemis.api.verses.decal.BemisImageDecal;
import at.petrak.bemis.api.verses.decal.BemisIngredientDecal;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.PositionalAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A convenience builder to make a {@link CenteredDecalVerse} that displays a recipe,
 * with a {@link at.petrak.bemis.api.verses.decal.BemisImageDecal} at the bottom and a bunch of
 * {@link BemisIngredientDecal}s
 * for the items.
 */
public class RecipeVerseBuilder {
    private final BemisDecalHolder decals;

    /**
     * @param image Pass {@code null} to use the book texture atlas.
     */
    public RecipeVerseBuilder(int width, int height, ItemStack result, int resultX, int resultY,
        @Nullable ResourceLocation image, int u, int v) {
        this.decals = new BemisDecalHolder(width, height);

        this.decals.addDecal(new BemisImageDecal(0, 0, width, height, image, u, v, width, height));

        this.decals.addDecal(new BemisIngredientDecal(resultX, resultY, Ingredient.of(result)));
    }

    public RecipeVerseBuilder add(Ingredient ingr, int x, int y) {
        this.decals.addDecal(new BemisIngredientDecal(x, y, ingr));
        return this;
    }

    public RecipeVerseBuilder addDecal(BemisDecal decal) {
        this.decals.addDecal(decal);
        return this;
    }

    public CenteredDecalVerse build() {
        return new CenteredDecalVerse(decals);
    }

    public static CenteredDecalVerse crafting(CraftingRecipe recipe) {
        var builder = new RecipeVerseBuilder(102, 64, recipe.getResultItem(), 81, 24, null,
            0, 144);

        var ingrs = recipe.getIngredients();
        int rw = 3, rh = 3;
        if (recipe instanceof ShapedRecipe shaped) {
            rw = shaped.getWidth();
            rh = shaped.getHeight();
        }
        for (int i = 0; i < ingrs.size(); i++) {
            int x = i % rw;
            int y = i / rw;
            builder.add(ingrs.get(i), 5 + x * (16 + 3), 5 + y * (16 + 3));
        }

        return builder.build();
    }

    @Name("bemis!recipe")
    @PositionalAttributes({"recipe"})
    public static final class RecipeMacro extends BlockMacroProcessor {
        @Override
        public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
            BemisVerse verse;
            String recipeIdStr = (String) attributes.get("recipe");
            var recipeLoc = ResourceLocation.tryParse(recipeIdStr);
            if (recipeLoc == null) {
                verse =
                    new ErrorVerse("bemis!recipe: the string `%s` is not a valid resource location".formatted(recipeIdStr));
            } else {
                var recipes = Minecraft.getInstance().level.getRecipeManager();

                switch (target) {
                    case "crafting" -> {
                        CraftingRecipe recipe = BemisApi.get().getRecipe(recipes, recipeLoc, RecipeType.CRAFTING);
                        if (recipe == null) {
                            verse =
                                new ErrorVerse("bemis!recipe: could not find a recipe for `%s`".formatted(recipeLoc));
                        } else {
                            verse = RecipeVerseBuilder.crafting(recipe);
                        }
                    }
                    default -> {
                        verse =
                            new ErrorVerse("bemis!recipe: unknown recipe type `%s`".formatted(recipeLoc));
                    }
                }

            }
            return BemisApi.get().makeVerseLiteralNode(this, parent, List.of(verse));
        }
    }
}
