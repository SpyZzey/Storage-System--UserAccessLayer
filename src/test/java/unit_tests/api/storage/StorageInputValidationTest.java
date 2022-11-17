package unit_tests.api.storage;

import de.storagesystem.api.storage.StorageInputValidation;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StorageInputValidationTest {

    @Test
    public void testBucketNameValidation() {
        assertFalse(StorageInputValidation.validateBucketName(""));
        assertFalse(StorageInputValidation.validateBucketName(null));
    }

    @Test
    public void testFolderPathValidation() {
        assertFalse(StorageInputValidation.validateFolderPath("/ThisIsNotAValidFolderPath"));
        assertFalse(StorageInputValidation.validateFolderPath("ThisIsAlsoNotAValidFolderPath/"));
    }

    @Test
    public void testFolderNameValidation() {
        assertFalse(StorageInputValidation.validateFolderName(""));
        assertFalse(StorageInputValidation.validateFolderName(null));
        assertFalse(StorageInputValidation.validateFolderName("ThisIsNotAValidFolderName/"));
        assertFalse(StorageInputValidation.validateFolderName("This/Is/Not/A/Valid/FolderName"));
        assertFalse(StorageInputValidation.validateFolderName("/ThisIsNotAValidFolderName"));
    }

    @Test
    public void testFileNameValidation() {
        assertFalse(StorageInputValidation.validateFileName(""));
        assertFalse(StorageInputValidation.validateFileName(null));
        assertFalse(StorageInputValidation.validateFileName("ThisIsNotAValidFileName/"));
        assertFalse(StorageInputValidation.validateFileName("This/Is/Not/A/Valid/FileName"));
        assertFalse(StorageInputValidation.validateFileName("/ThisIsNotAValidFileName"));
    }

}
