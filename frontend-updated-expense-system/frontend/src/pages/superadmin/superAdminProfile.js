import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import ChangePassword from "../../components/userProfile/changePassword";
import UserProfileCard from "../../components/userProfile/userProfileCard";

function SuperAdminProfile() {
  return (
    <Container activeNavId={21}>
      <Header title="Super Admin Settings" />
      <div className="settings-container">
        <UserProfileCard />
        <ChangePassword />
      </div>
    </Container>
  );
}

export default SuperAdminProfile;
