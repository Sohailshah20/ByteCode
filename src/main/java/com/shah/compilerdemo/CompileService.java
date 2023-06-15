package com.shah.compilerdemo;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompileService {

    private DockerClient dockerClient;

    private final String imageId = "a3562aa0b991";
    private String javaFilePath = "E:/temp/";
    private String classPath = "E:/temp/Hello.class";

    public CompileService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public List<String> runCommandInContainer() {
        ListImagesCmd images = dockerClient.listImagesCmd();
        List<Image> exec = images.exec();
        Image image = exec.get(0);
        List<String> imageNames = new ArrayList<>();
        for (int i = 0; i < exec.size(); i++) {
            for (String tag : exec.get(i).getRepoTags()) {
                String imageName = tag.split(":")[0]; // Extract the image name from the tag
                imageNames.add(imageName);
            }
        }
        return imageNames;
    }

    public String formatCode(String code) {
        StringBuilder fullcode = new StringBuilder("class Hello {");
        fullcode.append(code);
        fullcode.append("}");
        String fileName = "Hello.java";
        File newFile = new File("E:/temp/Hello.java");
        try  {
            if (!newFile.exists()){
                System.out.println("*********************************File is not created");
                newFile.createNewFile();
            }
            System.out.println("*********************************" + fullcode.toString());
            FileWriter writer = new FileWriter(newFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(fullcode.toString());

            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return compileCode("javac Hello.java");
    }

    public String compileCode(String command) {
        StringBuilder error = new StringBuilder();
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            CreateContainerResponse sh = createAndStartContainer(command);

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
        if (error.toString().isEmpty()) {
            return runProgram("java Hello");
        }else return error.toString();
    }

    public String runProgram(String command){
        System.out.println("*******************IN THE RUN METHOD");
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        try {
            CreateContainerResponse sh = createAndStartContainer(command);

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
        return !stderr.toString().isEmpty() ? stderr.toString() : stdout.toString();
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

    private static LogContainerResultCallback getLogContainerResultCallback(StringBuilder stdout, StringBuilder stderr) {
        LogContainerResultCallback logCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                if (item.getStreamType().equals(StreamType.STDOUT)) {
                    stdout.append(new String(item.getPayload()));
                    System.out.print("******************  STDOUT" +new String(item.getPayload()));
                } else if (item.getStreamType().equals(StreamType.STDERR)) {
                    stderr.append(new String(item.getPayload()));
                    System.err.print("******************  ERROR" +new String(item.getPayload()));
                }
            }
        };
        return logCallback;
    }

}
