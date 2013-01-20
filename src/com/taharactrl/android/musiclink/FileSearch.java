package com.taharactrl.android.musiclink;

import android.annotation.SuppressLint;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TreeSet;


/**
 * 
 */
public class FileSearch {

    public static final int TYPE_FILE_OR_DIR = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_DIR = 3;

    
    /**
     * �w�肵���f�B���N�g��[directoryPath]����A
     * �����Ώۂ̃t�@�C��[fileName]���ċA�I�Ɍ������A�Y������
     * �t�@�C���I�u�W�F�N�g�̃��X�g��Ԃ��܂��B
     * 
     * ��) 
     * File[] files =listFiles("C:/filelist/", "*.java");
     * ��L�̗�ł́A�f�B���N�g��filelist���ċA�I�Ɍ������A
     * �g���qjava�̃t�@�C�����X�g���擾���܂��B
     * 
     * @param directoryPath �����Ώۂ̃f�B���N�g����\���p�X
     * @param fileName �����Ώۂ̃t�@�C���� 
     *                 �t�@�C�����ɂ̓��C���h�J�[�h�����Ƃ���*���w��\
     * @return �����Ƀ}�b�`�����t�@�C���I�u�W�F�N�g
     */
    public File[] listFiles(String directoryPath, String fileName) {
        // ���C���h�J�[�h�����Ƃ���*�𐳋K�\���ɕϊ�
        if (fileName != null) {
            fileName = fileName.replace(".", "\\.");
            fileName = fileName.replace("*", ".*");
        }
        return listFiles(directoryPath, fileName, TYPE_FILE, true, 0);
    }

    /**
     * �w�肵���f�B���N�g��[directoryPath]����A���K�\���Ƃ��Ďw�肳�ꂽ
     * �����Ώۂ̃t�@�C��[fileNamePattern]���ċA�I�Ɍ������A
     * �Y������t�@�C���I�u�W�F�N�g�̃��X�g��Ԃ��܂��B
     * 
     * �܂��A�t�@�C���̍X�V���t���w������o�߂��Ă��邩�ǂ���������������
     * �w�肷�鎖���ł��܂��B
     * 
     * ��) 
     * File[] files = 
     *         listFiles("C:/filelist/", "*.java",TYPE_FILE, true, 2);
     * ��L�̗�ł́A�f�B���N�g��filelist���ċA�I�Ɍ������A7���O�ȍ~�ɍX�V
     * ���ꂽ�g���qjava�̃t�@�C�����X�g���擾���܂��B
     * 
     * @param directoryPath �����Ώۂ̃f�B���N�g����\���p�X
     * @param fileNamePattern �����Ώۂ̃t�@�C����[���K�\��]
     * @param type �Y������t�@�C���I�u�W�F�N�g��[type]�ɂ��A
     *                �ȉ��̎w�肪�\
     *                TYPE_FILE_OR_DIR�E�E�E�t�@�C���y�уf�B���N�g�� 
     *                TYPE_FILE�E�E�E�t�@�C��
     *                TYPE_DIR�E�E�E�f�B���N�g��
     * @param isRecursive �ċA�I�Ɍ�������ꍇ��true
     * @param period �����ΏۂƂ��āA�t�@�C���̍X�V���t���w������o��
     *                ���Ă��邩�ǂ�����ݒ�\
     *                0�̏ꍇ�͑ΏۊO
     *                1�ȏ�̏ꍇ�A�w������ȍ~�̃t�@�C���������ΏۂƂ���
     *                -1�ȉ��̏ꍇ�A�w������ȑO�̃t�@�C���������ΏۂƂ���
     * @return �����Ƀ}�b�`�����t�@�C���I�u�W�F�N�g
     */
    @SuppressWarnings("unchecked")
	public File[] listFiles(String directoryPath, 
            String fileNamePattern, int type, 
            boolean isRecursive, int period) {
        
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException
            ("�����Ŏw�肳�ꂽ�p�X[" + dir.getAbsolutePath() + 
                    "]�̓f�B���N�g���ł͂���܂���B");
        }
        File[] files = dir.listFiles();
        // ���̏o��
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            addFile(type, fileNamePattern, set, file, period);
            // �ċA�I�Ɍ������f�B���N�g���Ȃ�΍ċA�I�Ƀ��X�g�ɒǉ�
            if (isRecursive && file.isDirectory()) {
                listFiles(file.getAbsolutePath(), fileNamePattern, 
                            type, isRecursive, period);
            }
        }
        return (File[]) set.toArray(new File[set.size()]);
    }

    @SuppressLint("SimpleDateFormat")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addFile(int type, String match, TreeSet set,
            File file,int period) {
        switch (type) {
        case TYPE_FILE:
            if (!file.isFile()) {
                return;
            }
            break;
        case TYPE_DIR:
            if (!file.isDirectory()) {
                return;
            }
            break;
        }
        if (match != null && !file.getName().matches(match)) {
            return;
        }
        // �w������o�߂��Ă��邩�ǂ����̎w�肪����ꍇ
        if (period != 0) {
            // �t�@�C���X�V���t
            Date lastModifiedDate = new Date(file.lastModified());
            String lastModifiedDateStr = new SimpleDateFormat("yyyyMMdd")
                    .format(lastModifiedDate);

            // �w��̓��t�i�P�����~���b�Ōv�Z�j
            long oneDayTime = 24L * 60L * 60L * 1000L; 
            long periodTime = oneDayTime * Math.abs(period);
            Date designatedDate = 
                new Date(System.currentTimeMillis() - periodTime);
            String designatedDateStr = new SimpleDateFormat("yyyyMMdd")
                    .format(designatedDate);
            if (period > 0) {
                if (lastModifiedDateStr.compareTo(designatedDateStr) < 0) {
                    return;
                }
            } else {
                if (lastModifiedDateStr.compareTo(designatedDateStr) > 0) {
                    return;
                }
            }
        }
        // �S�Ă̏����ɊY������ꍇ���X�g�Ɋi�[
        set.add(file);

    }

    /** �A���t�@�x�b�g���ɕ��ׂ邽��TreeSet���g�p�B */
    @SuppressWarnings("rawtypes")
	private TreeSet set = new TreeSet();

    /**
     * �C���X�^���X�𐶐���A�����Ďg�p����ꍇ�́A���̃��\�b�h��
     * �Ăяo���N���A����K�v������B
     * ��)
     *  FileSearch search = new FileSearch();
     *  File[] f1 = search.listFiles(C:/filelist/", "*.java");
     *  search.clear();
     *  File[] f2 = search.listFiles("C:/filelist/", "*.jsp"); 
     */
    public void clear(){
    	set.clear();
    }
}
