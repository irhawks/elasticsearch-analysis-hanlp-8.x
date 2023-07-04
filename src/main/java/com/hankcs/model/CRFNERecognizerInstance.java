package com.hankcs.model;

import com.hankcs.cfg.HanlpPath;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFNERecognizer;
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
 * Create: 2021-01-30 01:22
 */
public class CRFNERecognizerInstance {

    private static final Logger logger = LogManager.getLogger(CRFNERecognizerInstance.class);

    private static volatile CRFNERecognizerInstance instance = null;

    public static CRFNERecognizerInstance getInstance() {
        if (instance == null) {
            synchronized (CRFNERecognizerInstance.class) {
                if (instance == null) {//二次检查
                    instance = new CRFNERecognizerInstance();
                }
            }
        }
        return instance;
    }

    private final CRFNERecognizer recognizer;

    private CRFNERecognizerInstance() {
        Path path = Paths.get(AccessController.doPrivileged((PrivilegedAction<String>) () -> HanlpPath.CRFNERModelPath)
        ).toAbsolutePath();
        if (FileSystemUtils.exists(path)) {
            recognizer = AccessController.doPrivileged((PrivilegedAction<CRFNERecognizer>) () -> {
                try {
                    return new CRFNERecognizer(HanlpPath.CRFNERModelPath);
                } catch (IOException e) {
                    logger.error(() -> new ParameterizedMessage("load crf ner model from [{}] error", HanlpPath.CRFNERModelPath), e);
                    return null;
                }
            });
        } else {
            logger.warn("can not find crf ner model from [{}]", HanlpPath.CRFNERModelPath);
            System.out.println(path);
            recognizer = null;
        }
    }

    public CRFNERecognizer getRecognizer() {
        return recognizer;
    }
}
