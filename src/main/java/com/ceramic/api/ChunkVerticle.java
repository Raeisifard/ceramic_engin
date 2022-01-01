package com.ceramic.api;

import com.github.diogoduailibe.lzstring4j.LZString;
import com.vx6.tools.MultipartStringMessage;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;

public class ChunkVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(ChunkVerticle.class);
    private String str = "";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        MultipartStringMessage msm = new MultipartStringMessage(vertx, config().getJsonObject("multiChunksMessage"));
        /*vertx.eventBus().consumer("test01", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                System.out.println("Untouched.res.result().length(): " + res.result().length());
            });
            fut.onFailure(res -> {
                System.out.println(res.toString());
            });
        });
        vertx.eventBus().consumer("test02", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                //byte[] data = ((String)((PromiseImpl) res).result()).getBytes();
                //ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                String decompressedUTF16 = LZString.decompressFromUTF16(res.result());
                System.out.println("Compress.res.result().length(): " + res.result().length());
                System.out.println("Uncompressed.res.result().length(): " + decompressedUTF16.length());
                //System.out.println(decompressedUTF16);
               *//* try (ZipInputStream zipStream = new ZipInputStream(byteStream)) {
                    // list files in zip
                    ZipEntry zipEntry = zipStream.getNextEntry();
                    while (zipEntry != null) {

                        boolean isDirectory = false;
                        // example 1.1
                        // some zip stored files and folders separately
                        // e.g data/
                        //     data/folder/
                        //     data/folder/file.txt
                        if (zipEntry.getName().endsWith(File.separator)) {
                            isDirectory = true;
                        }

                        //Path newPath = zipSlipProtect(zipEntry, target);

                        if (isDirectory) {
                            //Files.createDirectories(newPath);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipStream.read(buffer)) > 0) {
                                sb.append(new String(buffer, StandardCharsets.UTF_8));
                                //fos.write(buffer, 0, len);
                            }
                            System.out.println(sb);
                            // example 1.2
                            // some zip stored file path only, need create parent directories
                            // e.g data/folder/file.txt
                            *//**//*if (newPath.getParent() != null) {
                                if (Files.notExists(newPath.getParent())) {
                                    Files.createDirectories(newPath.getParent());
                                }
                            }
*//**//*
                            // copy files, nio
                            //Files.copy(zipStream, newPath, StandardCopyOption.REPLACE_EXISTING);

                            // copy files, classic
                    *//**//*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*//**//*
                        }

                        zipEntry = zipStream.getNextEntry();

                    }
                    zipStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
                //System.out.println("out: " + res);
            });
            fut.onFailure(res -> {
                System.out.println(res.toString());
            });
        });*/
        vertx.eventBus().consumer("test03", msg -> {
            Future<String> fut = msm.get(msg);
            fut.onComplete(res -> {
                //byte[] data = ((String)((PromiseImpl) res).result()).getBytes();
                //ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
                //String decompressedUTF16 = LZString.decompressFromUTF16(res.result());
                System.out.println("test03.length(): " + res.result().length());
                str = res.result();
                /*vertx.eventBus().request("test04", "get the very long compressed string", res2 -> {
                    Future fut2 = msm.send(res2.result(), res.result());
                    fut2.onComplete(res3 -> {
                        System.out.println("fut3 :" + res3.toString());
                    });
                    fut2.onFailure(res3 -> {
                        System.out.println(res3.toString());
                    });
                });*/

                vertx.eventBus().send("test05", "I have something for you");
                //System.out.println("Uncompressed.res.result().length(): " + decompressedUTF16.length());
                //System.out.println(decompressedUTF16);
               /* try (ZipInputStream zipStream = new ZipInputStream(byteStream)) {
                    // list files in zip
                    ZipEntry zipEntry = zipStream.getNextEntry();
                    while (zipEntry != null) {

                        boolean isDirectory = false;
                        // example 1.1
                        // some zip stored files and folders separately
                        // e.g data/
                        //     data/folder/
                        //     data/folder/file.txt
                        if (zipEntry.getName().endsWith(File.separator)) {
                            isDirectory = true;
                        }

                        //Path newPath = zipSlipProtect(zipEntry, target);

                        if (isDirectory) {
                            //Files.createDirectories(newPath);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zipStream.read(buffer)) > 0) {
                                sb.append(new String(buffer, StandardCharsets.UTF_8));
                                //fos.write(buffer, 0, len);
                            }
                            System.out.println(sb);
                            // example 1.2
                            // some zip stored file path only, need create parent directories
                            // e.g data/folder/file.txt
                            *//*if (newPath.getParent() != null) {
                                if (Files.notExists(newPath.getParent())) {
                                    Files.createDirectories(newPath.getParent());
                                }
                            }
*//*
                            // copy files, nio
                            //Files.copy(zipStream, newPath, StandardCopyOption.REPLACE_EXISTING);

                            // copy files, classic
                    *//*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*//*
                        }

                        zipEntry = zipStream.getNextEntry();

                    }
                    zipStream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //System.out.println("out: " + res);
            });
            fut.onFailure(res -> {
                System.out.println(res.toString());
            });
        });
        vertx.eventBus().consumer("test04", msg -> {
            msg.replyAndRequest(config().getJsonObject("multiChunksMessage"), new DeliveryOptions()
                    .addHeader("compression", config().getJsonObject("multiChunksMessage").getBoolean("compression").toString())
            .addHeader("chunkLength", config().getJsonObject("multiChunksMessage").getString("chunkLength")), messageAsyncResult -> {
                Future fut2 = msm.send(messageAsyncResult.result(), str);
                fut2.onComplete(res3 -> {
                    System.out.println("fut3 :" + res3.toString());
                });
                fut2.onFailure(res3 -> {
                    System.out.println(res3.toString());
                });
            });
        });
        startPromise.complete();
    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
}
