root_dir=/home/eason/AutoCancelProject
target_dir=solr/solr/solrj/src/java/org/apache/solr/common
class_prefix=org.apache.solr.common

cp -r $root_dir/autocancel_java_code/app/src/main/java/autocancel $root_dir/$target_dir/
sed -i "s/import autocancel/import $class_prefix.autocancel/g" $(find $root_dir/$target_dir/autocancel -name *.java)
sed -i "s/package autocancel/package $class_prefix.autocancel/g" $(find $root_dir/$target_dir/autocancel -name *.java)
