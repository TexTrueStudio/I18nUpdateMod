package org.cfpa.i18nupdatemod;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cfpa.i18nupdatemod.command.*;
import org.cfpa.i18nupdatemod.download.DownloadInfoHelper;
import org.cfpa.i18nupdatemod.download.DownloadStatus;
import org.cfpa.i18nupdatemod.download.RepoUpdateManager;
import org.cfpa.i18nupdatemod.git.ResourcePackRepository;
import org.cfpa.i18nupdatemod.hotkey.HotKeyHandler;
import org.cfpa.i18nupdatemod.installer.ResourcePackInstaller;
import org.cfpa.i18nupdatemod.notice.ShowNoticeFirst;
import org.cfpa.i18nupdatemod.resourcepack.ResourcePackBuilder;

import java.io.File;

import static org.cfpa.i18nupdatemod.I18nUtils.isChinese;
import static org.cfpa.i18nupdatemod.I18nUtils.setupLang;

@Mod(modid = I18nUpdateMod.MODID,
        name = I18nUpdateMod.NAME,
        clientSideOnly = true,
        acceptedMinecraftVersions = "[1.12]",
        version = I18nUpdateMod.VERSION,
        dependencies = "after:defaultoptions")
public class I18nUpdateMod {
    public final static String MODID = "i18nmod";
    public final static String NAME = "I18n Update Mod";
    public final static String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(MODID);

    private boolean shouldDisplayErrorScreen = false;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        // 国际化检查
        if (I18nConfig.internationalization.openI18n && !isChinese()) {
            return;
        }

        DownloadInfoHelper.init();

        // 设置中文
        if (I18nConfig.download.setupChinese) {
            setupLang();
        }

        if (!I18nConfig.download.shouldDownload) {
            return;
        }

        ResourcePackBuilder builder = new ResourcePackBuilder();
        boolean needUpdate = builder.checkUpdate();
        ResourcePackInstaller.setResourcesRepository();

        if (needUpdate) {
            String localPath;
            try {
                localPath = new File(I18nUtils.getLocalRepositoryFolder(I18nConfig.download.localRepoPath), "I18nRepo").getPath();
            } catch (IllegalArgumentException e) {
                shouldDisplayErrorScreen = true;
                return;
            }
            ResourcePackRepository repo = new ResourcePackRepository(localPath, builder.getAssetDomains());
            RepoUpdateManager updateManager = new RepoUpdateManager(repo);
            updateManager.update();
            if (updateManager.getStatus() == DownloadStatus.SUCCESS) {
                builder.updateAllNeededFilesFromRepo(repo);
                builder.touch();
                ShowNoticeFirst.shouldShowNotice = true;
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (shouldDisplayErrorScreen) {
            throw new I18nUtils.InvalidPathConfigurationException();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 国际化检查
        if (I18nConfig.internationalization.openI18n && !isChinese()) {
            return;
        }

        // 命令注册
        ClientCommandHandler.instance.registerCommand(new CmdNotice());
        ClientCommandHandler.instance.registerCommand(new CmdReport());
        ClientCommandHandler.instance.registerCommand(new CmdReload());
        ClientCommandHandler.instance.registerCommand(new CmdGetLangpack());
        ClientCommandHandler.instance.registerCommand(new CmdUpload());
        ClientCommandHandler.instance.registerCommand(new CmdToken());

        // 热键注册
        if (!I18nConfig.key.closedKey) {
            MinecraftForge.EVENT_BUS.register(new HotKeyHandler());
        }
    }
}
