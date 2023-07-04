package com.hankcs.model;

import com.hankcs.cfg.HanlpPath;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.perceptron.model.LinearModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.elasticsearch.common.io.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Project: elasticsearch-analysis-hanlp
 * Description:
 * Author: Kenn
 * Create: 2020-10-09 09:47
 */
public class PerceptronPOSInstance {

    private static final Logger logger = LogManager.getLogger(PerceptronPOSInstance.class);

    private static volatile PerceptronPOSInstance instance = null;

    public static PerceptronPOSInstance getInstance() {
        if (instance == null) {
            synchronized (PerceptronPOSInstance.class) {
                if (instance == null) {//二次检查
                    instance = new PerceptronPOSInstance();
                }
            }
        }
        return instance;
    }

    private final LinearModel linearModel;

    private PerceptronPOSInstance() {
        Path path= Paths.get(AccessController.doPrivileged((PrivilegedAction<String>) () -> HanlpPath.PerceptronPOSModelPath)
        ).toAbsolutePath();
        if (FileSystemUtils.exists(path)) {
            linearModel = AccessController.doPrivileged((PrivilegedAction<LinearModel>) () -> {
                try {
                    return new LinearModel(HanlpPath.PerceptronPOSModelPath);
                } catch (IOException e) {
                    logger.error(() ->
                            new ParameterizedMessage("load perceptron pos model from [{}] error", HanlpPath.PerceptronPOSModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find perceptron pos model from [{}]", HanlpPath.PerceptronPOSModelPath);
            System.out.println(path);
            linearModel = null;
        }
    }

    public LinearModel getLinearModel() {
        return linearModel;
    }
}
