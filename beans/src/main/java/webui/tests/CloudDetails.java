package webui.tests;

/**
 * User: guym
 * Date: 3/6/13
 * Time: 4:09 PM
 */
public class CloudDetails {


    public static enum CloudType{
        local, ec2, azure, byon, ec2_win("ec2-win"), hp, openstack, rsopenstack;

        String myName = null;
        CloudType(){
            myName = toString();
        }

        CloudType(String name){
            myName = name;
        }

    }

    private CloudType type = CloudType.local;

    public boolean isLocal(){
        return type == CloudType.local;
    }

    public String getType() {
        return isLocal() ? "localcloud" : "cloud";
    }

    public String getProvider(){
        return isLocal() ? "":type.myName;
    }

    public void setCloudType( CloudType type ) {
        this.type = type;
    }
}
