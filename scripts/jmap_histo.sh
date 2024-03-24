version=0
while true; do
    jmap -histo:live,file=$2/histo.data.tmp $1
    sed -i '1i '$version'' $2/histo.data.tmp
    mv $2/histo.data.tmp $2/histo.data
    version=$(($version+1))
    sleep 0.1
done
