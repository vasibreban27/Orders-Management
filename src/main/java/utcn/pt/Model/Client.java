package utcn.pt.Model;

public class Client {
    private int clientId;
    private String name;
    private String email;
    private String address;

    public Client(){}
    public Client(int id, String name, String email, String address) {
        this.clientId = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int id) {
        this.clientId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String toString(){
        return "Client with id "+this.clientId + " ,name " + this.name + " ,email: " + this.email + " ,live in" + this.address;
    }
}


