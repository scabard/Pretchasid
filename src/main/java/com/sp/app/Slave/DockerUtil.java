package com.sp.app.Slave;

import java.util.*;
import java.lang.*;
import java.io.File;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.*;
import com.spotify.docker.client.messages.ContainerConfig.*;
import com.spotify.docker.client.DockerClient.*;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.LogStream;

public class DockerUtil {
    public DefaultDockerClient client;

    public DockerUtil() throws Exception {
        this.client = DefaultDockerClient.fromEnv().build();
    }

    public void imgHandler( String image ) throws Exception {
        long match = this.client.listImages().stream().filter(img -> img.repoTags() != null && img.repoTags().contains(image)).count();
        if ( match == 0 ) {
            System.out.println("Pulling image");
            this.client.pull(image, (message) -> {
                if (message.progressDetail() != null && message.progressDetail().current()!=null && message.progressDetail().total()!=null ) {
                    long current = message.progressDetail().current();
                    long total = message.progressDetail().total();
                    System.out.println("Pulling image " + image + " " + current + " / " + total);
                } else {
                    System.out.println(message);
                }
            });
            System.out.println("Image pulled!");
        }
        else {
            System.out.println("Image Exists");
        }
    }

    String createContainer( String image ) throws Exception {
        final HostConfig hostConfig = HostConfig.builder()
                                                .build();

        final ContainerConfig containerConfig = ContainerConfig.builder()
                                                                .hostConfig(hostConfig)
                                                                .image(image)
                                                                .cmd("sh","-c","while :; do sleep 5000; done")
                                                                .build();

        final ContainerCreation creation = this.client.createContainer(containerConfig);
        return creation.id();
    }

    void checkContainerCreationState(String id) throws Exception {
        String error = this.client.inspectContainer(id).state().error();
        if (error != null && !error.equals("")) {
            throw new IllegalStateException("Container " + id + " creation failed:" + error);
        }
    }

    public void containerHandler( String image, String cmd ) throws Exception {
        final String id = createContainer( image );
        final String[] command = {"sh", "-c", "python /home/test.py"};
        this.client.copyToContainer(new File("data").toPath(), id, "/home/");

        this.client.startContainer(id);
        checkContainerCreationState(id);

        final ExecCreation execCreation = this.client.execCreate(
            id, command, DockerClient.ExecCreateParam.attachStdout(),
            DockerClient.ExecCreateParam.attachStderr());
        final LogStream output = this.client.execStart(execCreation.id());
        final String execOutput = output.readFully();

        System.out.println(execOutput);

        this.client.killContainer(id);
        this.client.removeContainer(id);
        this.client.close();
    }


}