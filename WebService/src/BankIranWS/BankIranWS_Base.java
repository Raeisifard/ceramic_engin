package BankIranWS;

public abstract class BankIranWS_Base {


    public class AccountInfoResponse {
        public boolean success = false;
        public String resultMessage = "";
        public String customerId = "";
        public String accountType = "";
        public String accountSubType = "";
        public String accountCurrency = "";
    }

    public class CustomerInfoResponse {
        public boolean success = false;
        public String resultMessage = "";
        public String customerMobileNo = "";
        public String customerType = "";
        public String getCustomerSubType = "";
    }

    public class DepositResponse {
        public boolean success = false;
        public String resultMessage = "";
        public long remain = -1;

        public String date;
        public String time;
        public int seqNo;
    }

    public class InquiryResponse {
        public boolean success = false;
        public String resultMessage = "";
        public boolean okDone;
    }

    public abstract AccountInfoResponse AccountInfo(String branchNo, String workstationId, String terminalId, String userId, String accountNo);

    public abstract CustomerInfoResponse CustomerInfo(String branchNo, String workstationId, String terminalId, String userId, String customerNo);

    public abstract DepositResponse deposit(String branchNo, String workstationId, String terminalId, String userId, String stmtCode, String sourceAccount, long amount);

    public abstract InquiryResponse inquiry(String account, String branchNo, String terminalId, String seqNo, String date, String time);

}
