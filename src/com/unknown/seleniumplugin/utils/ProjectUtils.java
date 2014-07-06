package com.unknown.seleniumplugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike-sid on 05.07.14.
 */
public class ProjectUtils {

    public static List<VirtualFile> getAllProjectFiles(Project project) {
        List<VirtualFile> projectFiles = new ArrayList<VirtualFile>();
        VirtualFile[] files = ProjectRootManager.getInstance(project).getContentSourceRoots();
        for (VirtualFile file : files) {
            addFiles(projectFiles, file);
        }
        return projectFiles;
    }


    private static void addFiles(final List<VirtualFile> projectFiles, VirtualFile file) {
        VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor(VirtualFileVisitor.NO_FOLLOW_SYMLINKS) {
            @Override
            public boolean visitFile(@NotNull VirtualFile file) {
                if (file instanceof VirtualFileImpl) {
                    projectFiles.add(file);
                }
                System.out.println(file.getUrl());
                return true;
            }
        });
    }

}
