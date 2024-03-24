root_dir=$(realpath $(git rev-parse --show-toplevel)/..)
target_dir=elasticsearch/server/src/main/java/org/elasticsearch
class_prefix=org.elasticsearch

cp -r $root_dir/autocancel_java_code/app/src/main/java/autocancel $root_dir/$target_dir/
sed -i "s/import autocancel/import $class_prefix.autocancel/g" $(find $root_dir/$target_dir/autocancel -name *.java)
sed -i "s/package autocancel/package $class_prefix.autocancel/g" $(find $root_dir/$target_dir/autocancel -name *.java)
