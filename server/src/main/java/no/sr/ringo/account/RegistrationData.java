package no.sr.ringo.account;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * User: Adam
 * Date: 4/3/12
 * Time: 8:57 AM
 */
//@XmlRootElement
public class RegistrationData implements Serializable {

    private final String name;
    private final String password;
    private final String username;

    private final String address1;
    private final String address2;
    private final String zip;
    private final String city;
    private final String country;
    private final String contactPerson;
    private final String email;
    private final String phone;
    private final String orgNo;

    private final Boolean registerSmp;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public Boolean isRegisterSmp() {
        return registerSmp;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public RegistrationData(String name, String password, String username, String address1, String address2, String zip, String city, String country, String contactPerson, String email, String phone, String orgNo, Boolean registerSmp) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.address1 = address1;
        this.address2 = address2;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.orgNo = orgNo;
        this.registerSmp = registerSmp;
    }

    public static RegistrationData fromJson(String JSONString) throws JSONException {
        JSONObject object = new JSONObject(JSONString);
        String name = object.getString("name");
        String password = object.getString("password");
        String address1 = object.getString("address1");
        String address2 = object.getString("address2");
        String zip = object.getString("zip");
        String city = object.getString("city");
        String country = object.getString("country");
        String contactPerson = object.getString("contactPerson");
        String email = object.getString("email");
        String phone = object.getString("phone");
        String username = object.getString("username");
        String orgNo = object.getString("orgNo");
        Boolean registerSmp = object.getBoolean("registerSmp");
        return new RegistrationData(name, password, username, address1, address2, zip, city, country, contactPerson, email, phone, orgNo, registerSmp);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistrationData that = (RegistrationData) o;

        if (address1 != null ? !address1.equals(that.address1) : that.address1 != null) return false;
        if (address2 != null ? !address2.equals(that.address2) : that.address2 != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (contactPerson != null ? !contactPerson.equals(that.contactPerson) : that.contactPerson != null) return false;
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (orgNo != null ? !orgNo.equals(that.orgNo) : that.orgNo != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (registerSmp != null ? !registerSmp.equals(that.registerSmp) : that.registerSmp != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (zip != null ? !zip.equals(that.zip) : that.zip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (address1 != null ? address1.hashCode() : 0);
        result = 31 * result + (address2 != null ? address2.hashCode() : 0);
        result = 31 * result + (zip != null ? zip.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (contactPerson != null ? contactPerson.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (orgNo != null ? orgNo.hashCode() : 0);
        result = 31 * result + (registerSmp != null ? registerSmp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegistrationData{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", zip='" + zip + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", orgNo='" + orgNo + '\'' +
                ", registerSmp=" + registerSmp +
                '}';
    }

}
