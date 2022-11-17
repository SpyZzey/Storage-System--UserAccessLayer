package unit_tests.api.storage;

import de.storagesystem.api.storage.StorageInputValidation;
import de.storagesystem.api.storage.StorageInputValidationImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StorageInputValidationTest {
    static StorageInputValidation inputValidation;

    @BeforeAll
    public static void init() {
        inputValidation = new StorageInputValidationImpl();
    }

    @Test
    public void testBucketNameValidation() {
        assertFalse(inputValidation.validateBucketName(""));
        assertFalse(inputValidation.validateBucketName(null));
    }

    @Test
    public void testFolderPathValidation() {
        assertFalse(inputValidation.validateFolderPath("/ThisIsNotAValidFolderPath"));
        assertFalse(inputValidation.validateFolderPath("ThisIsAlsoNotAValidFolderPath/"));
    }

    @Test
    public void testFolderNameValidation() {
        assertFalse(inputValidation.validateFolderName(""));
        assertFalse(inputValidation.validateFolderName(null));
        assertFalse(inputValidation.validateFolderName("ThisIsNotAValidFolderName/"));
        assertFalse(inputValidation.validateFolderName("This/Is/Not/A/Valid/FolderName"));
        assertFalse(inputValidation.validateFolderName("/ThisIsNotAValidFolderName"));
    }

    @Test
    public void testFileNameValidation() {
        assertFalse(inputValidation.validateFileName(""));
        assertFalse(inputValidation.validateFileName(null));
        assertFalse(inputValidation.validateFileName("ThisIsNotAValidFileName/"));
        assertFalse(inputValidation.validateFileName("This/Is/Not/A/Valid/FileName"));
        assertFalse(inputValidation.validateFileName("/ThisIsNotAValidFileName"));
    }

}
