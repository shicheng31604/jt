package com.jt.common.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;

import com.github.abel533.mapper.MapperProvider;
import com.github.abel533.mapperhelper.EntityHelper;
import com.github.abel533.mapperhelper.MapperHelper;

public class SysMapperProvider extends MapperProvider {

    private static final Class<?>[] Type = null;


	public SysMapperProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public SqlNode deleteByIDS(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        Set<EntityHelper.EntityColumn> entityColumns = EntityHelper.getPKColumns(entityClass);
        EntityHelper.EntityColumn column = null;
        for (EntityHelper.EntityColumn entityColumn : entityColumns) {
            column = entityColumn;
            break;
        }
        
        List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
        // 开始拼sql
        BEGIN();
        // delete from table
        DELETE_FROM(tableName(entityClass));
        // 得到sql
        String sql = SQL();
        // 静态SQL部分
        sqlNodes.add(new StaticTextSqlNode(sql + " WHERE " + column.getColumn() + " IN "));
        // 构造foreach sql
        SqlNode foreach = new ForEachSqlNode(ms.getConfiguration(), new StaticTextSqlNode("#{"
                + column.getProperty() + "}"), "ids", "index", column.getProperty(), "(", ")", ",");
        sqlNodes.add(foreach);
        return new MixedSqlNode(sqlNodes);
    }
    
    
    /****
     * 	测试通用Mapper的代码
     * 1.SqlNode  mybatis对sql语句的包装，最终mybatis通过解析sqlNone 使mysql执行操作
     * sql: select count(*) from XXX
     * 问题：如何获取表名？
     * 1.获取当前执行的mapper  itemMapper
     * 2.获取itemMapper继承的SysMapper
     * 3.获取SysMapper中的Item对象（泛型）
     * 4.通过获取Item对象 获取@Table注解
     * 5.获取注解中的name属性  即表名
     * pojo对象的@Table注解中含有name属性  name=tb_item
     * 
     * MappedStatement Mybatis自己的内置对象 
     */
    public SqlNode  findMapperCount(MappedStatement ms){
    	//1.获取方法调用的全路径   com.jt.manage.service.ItemServiceImpl.itemMapper.findMapperCount()
    	String path = ms.getId();
    	
    	//2.获取ItemMapper的路径   com.jt.manage.service.ItemServiceImpl.itemMapper
    	String mapperPath = path.substring(0, path.lastIndexOf("."));
    	
    	try {
    		//3.获取ItemMapper类型     通过反射获取对象类型
			Class<?> targetClass = Class.forName(mapperPath);
			
			//4.获取当前类型所继承的全部接口
			Type[]  types = targetClass.getGenericInterfaces();
			
			//5.获取Sysmapper
			Type type = types[0];
			
			//6.判断type类型是否为泛型接口    ParameterizedType   Item  List<sdf>
			if(type instanceof ParameterizedType) {
				//当前type为一个泛型
				//6.获取泛型类型
				ParameterizedType superType = (ParameterizedType) type;
				
				//7.获取泛型的参数
				Type[] argsType = superType.getActualTypeArguments();
				
				//8.获取一个参数  Item.Class
				Class<?> targetArgsCalss = (Class<?>) argsType[0];
				
				//9.判断是否还有@Table注解
				if(targetArgsCalss.isAnnotationPresent(Table.class)){
					
					//获取对象上的注解
					Table table = targetArgsCalss.getAnnotation(Table.class);
					
					//获取表名
					String tableName = table.name();
					
					String sql = "select count(*) from " + tableName;
					
					SqlNode sqlNode = new StaticTextSqlNode(sql);
					
					return sqlNode;
				}
				
			}
			
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
    	return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    

}
