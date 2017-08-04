package cn.runnerup.mybatis.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

public class EsPlugin extends PluginAdapter{
	
	private static boolean openEs = false;

	public boolean validate(List<String> warnings) {
		System.out.println("validate ++++++++++++++++++++");
		// TODO Auto-generated method stub
		return true;
	}

	
	
	@Override
	public void setContext(Context context) {
		if(context.getProperties().containsKey("openEs")){
			openEs = context.getProperty("openEs").equals("true");
		}
		super.setContext(context);
	}



	@Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
		if(!openEs)return true;
		
		addClassESAnnoation(topLevelClass, introspectedTable);
		
		generateEsJavaFile(topLevelClass, introspectedTable);
        return true;
    }
	
	private void addClassESAnnoation(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable){
		String className = topLevelClass.getType().getShortName();
		StringBuilder anno = new StringBuilder();
		anno.append("@Document(");
		
		
		anno.append("indexName=");//begin add indexname
		anno.append("\"testindex\"");//end add indexname
		
		anno.append(",");
		
		anno.append("type=");//begin add type
		anno.append("\"");
		anno.append(className);
		anno.append("\"");//end add type
		
		anno.append(")");
		
		topLevelClass.addAnnotation(anno.toString());
		topLevelClass.addImportedType("org.springframework.data.elasticsearch.annotations.Document");
		
	}
	
	private void generateEsJavaFile(TopLevelClass topLevelClass,IntrospectedTable introspectedTable){
		String esProject = "src\\main\\java";
		String esPackage = "cn.ft.model.generator.test";
		String className = topLevelClass.getType().getShortName();
		String esClassName = className+"Repository";
		StringBuilder es = new StringBuilder();
		es.append("package "+esPackage+";");
		es.append("\n");
		es.append("\n");
		es.append("import ");
		es.append(topLevelClass.getType().getPackageName());
		es.append(".");
		es.append(className);
        es.append(';');
        es.append("\n");
        es.append("import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;");
        es.append("\n");
        es.append("\n");
		
		es.append("public class ");
		es.append(esClassName);
		es.append(" extends ");
		es.append("ElasticsearchRepository<");
		es.append(className);
		es.append(",Long> {");
		es.append("\n");
		es.append("}");
		
		try {
			DefaultShellCallback shellCallback = new DefaultShellCallback(true);
			File targetFile = shellCallback.getDirectory(esProject, esPackage);
			File directory = new File(targetFile,esClassName+".java");
			writeFile(directory, es.toString(), "UTF-8");
		} catch (ShellException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }
        
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

}
