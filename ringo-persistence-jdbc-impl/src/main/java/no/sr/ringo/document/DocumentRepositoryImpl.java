package no.sr.ringo.document;

import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Repository;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.utils.SbdhUtils;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DocumentRepositoryImpl implements DocumentRepository {

    private final PeppolDocumentFactory documentFactory;
    private final JdbcTxManager jdbcTxManager;

    @Inject
    public DocumentRepositoryImpl(PeppolDocumentFactory documentFactory,JdbcTxManager jdbcTxManager) {
        this.documentFactory = documentFactory;
        this.jdbcTxManager = jdbcTxManager;
    }

    @Override
    public PeppolDocument getPeppolDocument(RingoAccount ringoAccount, MessageNumber msgNo) {
        try {
            return fetchPeppolDocument(ringoAccount, msgNo);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + msgNo, e);
        }
    }

    //
    // utility functions should be kept private to avoid being intercepted by the Repository annotation
    //

    private PeppolDocument fetchPeppolDocument(RingoAccount ringoAccount, MessageNumber msgNo) throws SQLException {
        ResultSet rs = fetchResultSet(ringoAccount, msgNo);
        if (documentFound(rs)) {
            return extractPeppolDocumentFromResultSet(rs);
        } else {
            throw new PeppolMessageNotFoundException(msgNo.toInt());
        }
    }

    private boolean documentFound(ResultSet rs) throws SQLException {
        return rs.next();
    }

    private ResultSet fetchResultSet(RingoAccount ringoAccount, MessageNumber msgNo) throws SQLException {
        final Connection con = jdbcTxManager.getConnection();
        PreparedStatement ps = prepareSelect(ringoAccount, msgNo, con);
        return ps.executeQuery();
    }

    private PeppolDocument extractPeppolDocumentFromResultSet(ResultSet rs) throws SQLException {
        String documentId = rs.getString("document_id");
        String xmlMessage = SbdhUtils.removeSbdhEnvelope(rs.getString("xml_message"));
        return documentFactory.makePeppolDocument(PeppolDocumentTypeId.valueFor(documentId), xmlMessage);
    }

    private PreparedStatement prepareSelect(RingoAccount ringoAccount, MessageNumber msgNo, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select document_id, xml_message from message where msg_no=? and account_id = ?");
        ps.setInt(1, msgNo.toInt());
        ps.setInt(2, ringoAccount.getId().toInteger());
        return ps;
    }

}
