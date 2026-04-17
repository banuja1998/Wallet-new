import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast, { Toaster } from "react-hot-toast";
import Container from "../../components/utils/Container";
import Header from "../../components/utils/header";
import Loading from "../../components/utils/loading";
import Search from "../../components/utils/search";
import PageInfo from "../../components/utils/pageInfo";
import Info from "../../components/utils/Info";
import usePagination from "../../hooks/usePagination";
import SuperAdminService from "../../services/superAdminService";

function ManageAdmins() {
  const [admins, setAdmins] = useState([]);
  const [isFetching, setIsFetching] = useState(true);
  const {
    pageSize,
    pageNumber,
    noOfPages,
    searchKey,
    onNextClick,
    onPrevClick,
    setNoOfPages,
    setNoOfRecords,
    setSearchKey,
    getPageInfo,
  } = usePagination();

  const loadAdmins = async () => {
    setIsFetching(true);
    await SuperAdminService.getAdmins(pageNumber, pageSize, searchKey).then(
      (response) => {
        if (response.data.status === "SUCCESS") {
          const payload = response.data.response;
          const records = payload?.data || payload?.content || [];
          setAdmins(records);
          setNoOfPages(payload?.totalNoOfPages || payload?.totalPages || 1);
          setNoOfRecords(payload?.totalNoOfRecords || payload?.totalElements || records.length);
          return;
        }
        toast.error("Failed to fetch admins");
      },
      () => {
        toast.error("Failed to fetch admins: Try again later!");
      },
    );
    setIsFetching(false);
  };

  useEffect(() => {
    loadAdmins();
  }, [pageNumber, searchKey]);

  return (
    <Container activeNavId={17}>
      <Header title="Manage Admins" />
      <Toaster />

      <div className="utils page">
        <Search onChange={(value) => setSearchKey(value)} placeholder="Search admins" />
        <PageInfo
          info={getPageInfo()}
          onPrevClick={onPrevClick}
          onNextClick={onNextClick}
          pageNumber={pageNumber}
          noOfPages={noOfPages}
        />
      </div>

      <div className="utils page" style={{ marginTop: "-5px", marginBottom: "20px" }}>
        <div>
          <p style={{ margin: 0 }}>Super admin can assign one or more admins to organizations.</p>
        </div>
        <Link to="/superadmin/admins/new">
          <button>Create Admin</button>
        </Link>
      </div>

      {isFetching && <Loading />}
      {!isFetching && admins.length === 0 && <Info text="No admins found!" />}
      {!isFetching && admins.length !== 0 && (
        <table>
          <tr>
            <th>Admin Id</th>
            <th>Username</th>
            <th>Email</th>
            <th>Organization</th>
            <th>Status</th>
          </tr>
          {admins.map((item) => (
            <tr key={item.id}>
              <td>{"A" + String(item.id).padStart(5, "0")}</td>
              <td>{item.username}</td>
              <td>{item.email}</td>
              <td>{item.organizationName || item.organization?.name || "-"}</td>
              <td style={{ color: item.enabled === false ? "#ff0000" : "#6aa412" }}>
                {item.enabled === false ? "Disabled" : "Enabled"}
              </td>
            </tr>
          ))}
        </table>
      )}
    </Container>
  );
}

export default ManageAdmins;
