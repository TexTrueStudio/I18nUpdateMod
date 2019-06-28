package org.cfpa.i18nupdatemod.git;

import java.io.File;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;


public class Repository {
	private String remoteUrl;
	private File localPath;
	private Git gitRepo=null;
	private String branch;
	
	public Repository(String remoteUrl, String localPath) {
		this.remoteUrl=remoteUrl;
		this.localPath=new File(localPath);
		this.branch="origin/1.12.2";
	}
	
	public void init() {
		if(localPath.exists()) {
			try {
				gitRepo=Git.open(localPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(gitRepo==null) {
			try {
				gitRepo=Git.cloneRepository().setURI(remoteUrl).setDirectory(localPath).setNoCheckout(true).call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		gitRepo.getRepository().close();
	}
	
	public void sparseCheckOut(Collection<String> subPathSet) {
		if(subPathSet.size()==0)
			return;
		CheckoutCommand checkoutCommand = gitRepo.checkout().setName(branch).setStartPoint(branch);
		for(String subPath : subPathSet) {
			checkoutCommand=checkoutCommand.addPath(subPath);
		}
		try {
			checkoutCommand.call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static String getSubPathOfAsset(String domain) {
		return "project/assets/"+domain;
	}

	public static Collection<String> getSubPathsOfAssets(Set<String> assetDomains) {
		return assetDomains.stream().map(Repository::getSubPathOfAsset).collect(Collectors.toSet());
		
	}
	
	public File getLocalPath() {
		return localPath;
	}

}