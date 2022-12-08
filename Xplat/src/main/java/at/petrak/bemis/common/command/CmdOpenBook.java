package at.petrak.bemis.common.command;

import at.petrak.bemis.api.BemisApi;
import at.petrak.bemis.common.msg.MsgOpenBookS2C;
import at.petrak.bemis.xplat.Xplat;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CmdOpenBook {
    public static void add(LiteralArgumentBuilder<CommandSourceStack> cmd) {
        cmd.then(Commands.literal("open-book")
            .then(Commands.argument("book-name", ResourceLocationArgument.id())
                .executes(ctx -> {
                    var user = ctx.getSource().getPlayerOrException();
                    var bookName = ctx.getArgument("book-name", ResourceLocation.class);
                    var book = BemisApi.get().getBook(bookName);
                    if (book == null) {
                        ctx.getSource().sendFailure(Component.literal("no book named " + bookName));
                    } else {
                        Xplat.get().sendPacketToPlayer(user, new MsgOpenBookS2C(bookName));
                    }
                    return 1;
                })));
    }
}
