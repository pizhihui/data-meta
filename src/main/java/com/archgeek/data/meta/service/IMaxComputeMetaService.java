package com.archgeek.data.meta.service;

/**
 * @author pizhihui
 * @date 2024-10-28 10:39
 */
public interface IMaxComputeMetaService {


    void processSchema();

    void processTables();

    void processColumns();

    void processPartitions();



}
