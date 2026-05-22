package ulb.services;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.common.dto.persistence.CreateBugemonDTO;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.repositories.BugemonRepository;
import ulb.repositories.StaticRepository;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;

public class TestBugemonService {

    private StaticRepository staticRepo;
    private BugemonRepository bugemonRepo;
    private BugemonService service;

    @Before
    public void setUp() {
        this.staticRepo = mock(StaticRepository.class);
        this.bugemonRepo = mock(BugemonRepository.class);
        this.service = new BugemonService(this.staticRepo, this.bugemonRepo, "testPlayer");
    }

    @Test
    public void shouldThrowBugemonNameIsEmptyException_whenNameIsEmpty() {
        CreateBugemonDTO dto = new CreateBugemonDTO("", ElementType.PYRO, null, 10, 10, 10, 100, false, List.of());
        when(this.staticRepo.bugemons()).thenReturn(List.of());

        assertThrows(BugemonNameIsEmptyException.class, () -> this.service.saveNewBugemon(dto));
    }

    @Test
    public void shouldThrowBugemonNameIsEmptyException_whenNameIsBlankSpaces() {
        CreateBugemonDTO dto = new CreateBugemonDTO("", ElementType.AQUA, null, 5, 5, 5, 50, false, List.of());
        when(this.staticRepo.bugemons()).thenReturn(List.of());

        assertThrows(BugemonNameIsEmptyException.class, () -> this.service.saveNewBugemon(dto));
    }

    @Test
    public void shouldThrowBugemonNameAlreadyExistsException_whenNameAlreadyExists() {
        Bugemon existingBugemon = mock(Bugemon.class);
        when(existingBugemon.name()).thenReturn("Flamby");
        when(this.staticRepo.bugemons()).thenReturn(List.of(existingBugemon));

        CreateBugemonDTO dto = new CreateBugemonDTO("Flamby", ElementType.PYRO, null, 10, 10, 10, 100, false,
                List.of());

        assertThrows(BugemonNameAlreadyExistsException.class, () -> this.service.saveNewBugemon(dto));
    }

    @Test
    public void shouldThrowBugemonNameAlreadyExistsException_whenNameMatchesExistingCaseSensitive() {
        Bugemon existingBugemon = mock(Bugemon.class);
        when(existingBugemon.name()).thenReturn("Aquamon");
        when(this.staticRepo.bugemons()).thenReturn(List.of(existingBugemon));

        CreateBugemonDTO dto = new CreateBugemonDTO("Aquamon", ElementType.AQUA, null, 5, 5, 5, 80, false, List.of());

        assertThrows(BugemonNameAlreadyExistsException.class, () -> this.service.saveNewBugemon(dto));
    }

    @Test
    public void shouldSaveBugemon_whenNameIsUniqueAndNonEmpty()
            throws BugemonNameIsEmptyException, BugemonNameAlreadyExistsException {
        Bugemon existingBugemon = mock(Bugemon.class);
        when(existingBugemon.name()).thenReturn("Flamby");
        when(this.staticRepo.bugemons()).thenReturn(List.of(existingBugemon));

        CreateBugemonDTO dto = new CreateBugemonDTO("Terrabyte", ElementType.LITHO, null, 8, 8, 8, 90, false,
                List.of());

        this.service.saveNewBugemon(dto);

        verify(this.staticRepo).saveBugemon(dto);
    }

    @Test
    public void shouldSaveBugemon_whenRepoIsEmpty()
            throws BugemonNameIsEmptyException, BugemonNameAlreadyExistsException {
        when(this.staticRepo.bugemons()).thenReturn(List.of());

        CreateBugemonDTO dto = new CreateBugemonDTO("Newmon", ElementType.PYRO, null, 6, 6, 6, 60, false, List.of());

        this.service.saveNewBugemon(dto);

        verify(this.staticRepo).saveBugemon(dto);
    }
}
