package org.cfpa.i18nupdatemod.git;
/*
import static org.cfpa.i18nupdatemod.I18nUpdateMod.logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cfpa.i18nupdatemod.I18nConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;

public class ResourcePackRepository {
    private static String[] remoteURLs;
    private File localPath;
    public Git gitRepo = null;
    private String branch;
    private Set<String> assetDomains;

    public ResourcePackRepository(String localPath, Set<String> assetDomains) {
        remoteURLs = I18nConfig.download.remoteRepoURL;
        this.localPath = new File(localPath);
        this.assetDomains = assetDomains;
        this.branch = "1.12.2-release";
        initRepo();
    }

    private void initRepo() {
        if (localPath.exists()) {
            try {
                gitRepo = Git.open(localPath);
            } catch (Exception e) {
                logger.error("Exception caught while initializing git repository: ", e);
            }
        }
        if (gitRepo == null) {
            try {
                gitRepo = Git.init().setDirectory(localPath).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void configRemote(String name, String url, String branchName) {
        try {
            StoredConfig config = gitRepo.getRepository().getConfig();
            config.setString("remote", name, "url", url);
            config.setString("remote", name, "fetch",
                    "+refs/heads/" + branchName + ":refs/remotes/origin/" + branchName);
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        gitRepo.getRepository().close();
    }

    private boolean fetchFromRemote(String remoteName, ProgressMonitor monitor) {
        try {
            // fetch
            gitRepo.fetch()
                    .setProgressMonitor(monitor)
                    .setRemote(remoteName)
                    .call();
            return true;
        } catch (Exception e) {
            logger.error("Invalid remote repository: ", e);
            return false;
        }
    }

    public void fetch(ProgressMonitor monitor) {
        boolean success;
        List<RemoteConfig> remoteList = new ArrayList<>();
        try {
            remoteList = gitRepo.remoteList().call();
        } catch (Exception e) {
            logger.error("Error while getting remote list: ", e);
        }
        for (RemoteConfig remoteConfig : remoteList) {
            // TODO 检查连接情况
            success = fetchFromRemote(remoteConfig.getName(), monitor);
            if (success) {
                return;
            }
        }

        for (int i = 0; i < remoteURLs.length; i++) {
            String remoteName = "origin" + i;
            String gitURL = remoteURLs[i];

            // 解析 HTML 重定向，HTTP 302 重定向用不了
            if (remoteURLs[i].endsWith("html") || remoteURLs[i].endsWith("htm")) {
                CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(remoteURLs[i]);
                CloseableHttpResponse response;
                try {
                    response = closeableHttpClient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                        int index = result.indexOf("meta http-equiv=\"refresh\"");
                        int startIndex = index + "meta http-equiv=\"refresh\" content=\"0;url=".length();
                        int endIndex = result.indexOf("\">", startIndex);
                        gitURL = result.substring(startIndex, endIndex);
                        closeableHttpClient.close();
                    } else {
                        closeableHttpClient.close();
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("Error while fetching resource pack repository: ", e);
                    continue;
                }
            }

            configRemote(remoteName, gitURL, this.branch);
            success = fetchFromRemote(remoteName, monitor);

            if (success) {
                return;
            }
        }
        logger.warn("仓库更新失败");
    }
    
    public void reset(ProgressMonitor monitor) {
        try {
            // create branch and set upstream
            gitRepo.branchCreate()
                    .setName(branch)
                    .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
                    .setStartPoint("origin/" + branch)
                    .setForce(true)
                    .call();
            
            // reset to remote head
            gitRepo.reset()
                    .setProgressMonitor(monitor)
                    .setMode(ResetType.SOFT)
                    .setRef("refs/remotes/origin/" + branch)
                    .call();
        } catch (Exception e) {
            logger.error("Exception caught while reseting to remote head: ", e);
        }
    }

    public void sparseCheckout(Collection<String> subPathSet, ProgressMonitor monitor) {
        try {
            // sparse checkout
            CheckoutCommand checkoutCommand = gitRepo.checkout();

            checkoutCommand.setProgressMonitor(monitor)
                    .setName(branch)
                    .setStartPoint(branch);

            subPathSet.forEach(checkoutCommand::addPath);
            checkoutCommand.call();
        } catch (Exception e) {
            logger.error("Exception caught while checking out: ", e);
        }
    }
    
    public void sparseCheckout(String subPath, ProgressMonitor monitor) {
        try {
            // sparse checkout
            gitRepo.checkout()
                    .setProgressMonitor(monitor)
                    .setName(branch)
                    .setStartPoint(branch)
                    .addPath(subPath)
                    .call();
        } catch (Exception e) {
            logger.error("Exception caught while checking out: ", e);
        }
    }

    public static String getSubPathOfAsset(String domain) {
        return "assets/" + domain;
    }

    public Collection<String> getSubPaths() {
        return assetDomains.stream().map(ResourcePackRepository::getSubPathOfAsset).collect(Collectors.toSet());
    }

    public File getLocalPath() {
        return localPath;
    }
}
 */
