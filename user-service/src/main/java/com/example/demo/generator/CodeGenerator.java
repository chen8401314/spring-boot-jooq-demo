package com.example.demo.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.List;

/**
 * @author shenqicheng
 */
public class CodeGenerator {

    public static void main(String[] args) {
        List<String> tables = List.of(
                "pf_construction_unit_type",
                "pf_construction_unit_prop",
                "pf_wbs_construction_relation",
                "pf_wbs_construction_cycle_relation",
                "pf_wbs_construction_prop_relation",
                "pf_wbs_task_relation",
                "pf_wbs_cycle_task_relation",
                "pf_rule_task",
                "pf_rule",
                "pf_element_relation",
                "pf_wbs_cycle_relation"
        );

        String[] superColumns = {"id", "create_id", "create_by", "create_time", "update_time", "update_id", "update_by"};
        for (String table : tables) {
            FastAutoGenerator.create("jdbc:mysql://localhost:10086/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false",
                            "root",
                            "mysqladm")
                    .globalConfig(builder -> {
                        builder.author("华规软件（上海）有限公司")
                                .disableOpenDir()
                                .enableSwagger()
                                .fileOverride()
                                .outputDir(System.getProperty("user.dir") + "/mybatis-plus-demo/src/main/java");
                    })
                    .packageConfig(builder -> {
                        builder.parent("com.qdmetro")
                                .moduleName("core")
                                .serviceImpl("service")
                                .mapper("repository")
                                .pathInfo(
                                        new HashMap<OutputFile,String>(8){{
                                            put(OutputFile.mapper,System.getProperty("user.dir") + "/mybatis-plus-demo/src/main/resources/mapper");
                                        }}
                                ); // 设置mapperXml生成路径
                    })
                    .strategyConfig(builder -> {
                        builder.addInclude(table)
                                .addTablePrefix("pf_")
                                // entity配置
                                .entityBuilder()
                                .convertFileName(name -> String.format("%sEntity", name))
                                .superClass("com.example.demo.entity.BaseEntity")
                                .idType(IdType.AUTO)
                                .addSuperEntityColumns(superColumns)
                                .naming(NamingStrategy.underline_to_camel)
                                .columnNaming(NamingStrategy.underline_to_camel)
                                .enableTableFieldAnnotation()
                                .disableSerialVersionUID()
                                .enableLombok()
                                .enableRemoveIsPrefix()
                                // controller配置
                                .controllerBuilder()
                                .enableRestStyle()
                                .enableHyphenStyle()
                                // mapper配置
                                .mapperBuilder()
                                .convertMapperFileName(name -> String.format("%sMapper", name))
                                .enableBaseResultMap()
                                .enableBaseColumnList()
                                .serviceBuilder()
                               //.convertServiceFileName(name -> name + ConstVal.SERVICE)
                                .convertServiceImplFileName(name -> name + ConstVal.SERVICE);
                    }).templateConfig(builder -> {
                        builder.disable(TemplateType.SERVICE);
                    })
                    .templateEngine(new VelocityTemplateEngine())
                    .execute();
        }
    }

}
