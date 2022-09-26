package models;

public class UpdateUserModel {

    private String name;

    private String gender;

    private String email;

    private String status;

    public UpdateUserModel(Builder builder) {
        this.name = builder.name;
        this.gender = builder.gender;
        this.email = builder.email;
        this.status = builder.status;
    }


    public String getName() {
        return name;
    }


    public String getGender() {
        return gender;
    }


    public String getEmail() {
        return email;
    }


    public String getStatus() {
        return status;
    }

    public static class Builder{
        private String name, gender, email, status;

        public Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder gender(String gender){
            this.gender = gender;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public UpdateUserModel build(){
            return new UpdateUserModel(this);
        }
    }
}
