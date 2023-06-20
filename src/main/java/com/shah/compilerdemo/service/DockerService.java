package com.shah.compilerdemo.service;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DockerService {

    private final DockerClient dockerClient;
    private final FileOperationsService fileService;

    private final String imageId = "9761a8b85d89";
    private final String javaFilePath = "E:/temp/";
    private final String classPath = "E:/temp/Hello.class";

    public DockerService(DockerClient dockerClient, FileOperationsService fileService) {
        this.dockerClient = dockerClient;
        this.fileService = fileService;
    }

    public List<String> runCommandInContainer() {
        ListImagesCmd images = dockerClient.listImagesCmd();
        List<Image> exec = images.exec();
        List<String> imageNames = new ArrayList<>();
        for (int i = 0; i < exec.size(); i++) {
            for (String tag : exec.get(i).getRepoTags()) {
                String imageName = tag.split(":")[0]; // Extract the image name from the tag
                imageNames.add(imageName);
            }
        }
        return imageNames;
    }
        public String compileCode(String fileName) {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            CreateContainerResponse sh = createAndStartContainer("javac "+fileName);

            dockerClient.startContainerCmd(sh.getId()).exec();

            LogContainerResultCallback logCallback = getLogContainerResultCallback(stdout, stderr);
            //logging the console statements
            logContainerConsoleOutput(sh, logCallback);
            //wait for container to finish executing the command
            waitForContainerExecution(sh);
            //remove the container
            removeContainer(sh);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (stderr.toString().isEmpty()) {
            System.out.println("*******************ERROR MESSAGE STRING " + stderr.toString());
            System.out.println("*******************SUB FILE NAME " + subFileName(fileName));
            return runProgram(subFileName(fileName));
        } else {
            fileService.deleteFile(javaFilePath, "Hello.java", null);
            System.out.println("*******************RETURNING COMPILE ERROR");
            return stderr.toString();
        }
    }

    private String subFileName(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, dotIndex);

    }

    public String runProgram(String fileName) {
        System.out.println("*******************FILE NAME: " + fileName);
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            CreateContainerResponse sh = createAndStartContainer("java "+fileName);

            dockerClient.startContainerCmd(sh.getId()).exec();

            LogContainerResultCallback logCallback = getLogContainerResultCallback(stdout, stderr);
            //logging the console statements
            logContainerConsoleOutput(sh, logCallback);
            //wait for container to finish executing the command
            waitForContainerExecution(sh);
            //remove the container
            removeContainer(sh);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!stderr.toString().isEmpty()) {
            fileService.deleteFile(javaFilePath, "Hello.java", "Hello.class");
            return stderr.toString();
        } else {
            fileService.deleteFile(javaFilePath, "Hello.java", "Hello.class");
            return stdout.toString();
        }
    }

    private void removeContainer(CreateContainerResponse sh) {
        dockerClient.removeContainerCmd(sh.getId()).exec();
    }

    private void logContainerConsoleOutput(CreateContainerResponse sh, LogContainerResultCallback logCallback) {
        dockerClient.logContainerCmd(sh.getId())
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(logCallback);
    }

    private CreateContainerResponse createAndStartContainer(String command) {
        CreateContainerResponse sh = dockerClient.createContainerCmd(imageId)
                .withName("javajdk")
                .withBinds(new Bind(javaFilePath, new Volume("/app/")))
                .withCmd("sh", "-c", command)
                .withWorkingDir("/app")
                .exec();
        return sh;
    }


    private void waitForContainerExecution(CreateContainerResponse sh) throws InterruptedException {
        dockerClient.waitContainerCmd(sh.getId())
                .exec(new WaitContainerResultCallback())
                .awaitCompletion();
    }

    private static LogContainerResultCallback getLogContainerResultCallback(StringBuilder stdout, StringBuilder
            stderr) {
        LogContainerResultCallback logCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType().equals(StreamType.STDOUT)) {
                    stdout.append(new String(item.getPayload()));
                    System.out.print("******************  STDOUT" + new String(item.getPayload()));
                } else if (item.getStreamType().equals(StreamType.STDERR)) {
                    stderr.append(new String(item.getPayload()));
                    System.err.print("******************  ERROR" + new String(item.getPayload()));
                }
            }
        };
        return logCallback;
    }
}
